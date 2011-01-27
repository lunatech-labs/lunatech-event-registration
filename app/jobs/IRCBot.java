package jobs;

import com.lunatech.events.StaticHolder;
import models.Event;
import org.jibble.pircbot.PircBot;
import play.Logger;
import play.Play;
import play.Play.Mode;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRCBot extends PircBot {

	private final String infoCmdPattern = "^\\s*info\\s+(\\S+)\\s*$";
	private final String eventsCmdPattern = "^\\s*events\\s*$";
	private final String helpCmdPattern = "^\\s*help\\s*$";

	private IRCBot(){
		setName(Play.configuration.getProperty("irc.nick", "EventBot"));
	}

	/**
	 * Listens for commands in private message and responds accordingly.
	 * <p>Known commands are:
	 * <ul><li>info</li><li>events</li><li>help</li></ul></p>
	 * @param sender the original sender of the message
	 * @param login not used
	 * @param hostname not used
	 * @param message the actual message (hopefully containing a command)
	 */
	@Override
	protected void onPrivateMessage(final String sender, final String login, final String hostname, final String message) {
		if (message.matches(this.infoCmdPattern)) {
			Matcher matcher = Pattern.compile(this.infoCmdPattern).matcher(message);
			matcher.matches();

			final String eventId = matcher.group(1);

			Event event;
			EntityManager entityManager = null;
			try {
				entityManager = JPA.newEntityManager();
				final Query query = entityManager.createQuery("from Event where id = ?", Event.class);
				query.setParameter(1, eventId);
				final List<Event> resultList = query.getResultList();
				if (resultList.size() == 0) {
					sendMessage(sender, String.format("No event found with id [%s]", eventId));
					return;
				}

				if (resultList.size() > 1) {
					Logger.error("Found more than 1 event with id [%s]");
					sendMessage(sender, String.format("More than 1 event found with id [%s], something is broken", eventId));
					return;
				}

				event = (Event) resultList.get(0);

				sendMessage(sender, String.format("Event [%s] has [%d] registered participants", event.title,
					event.participants.size()));
			} catch (Exception e) {
				Logger.error(String.format("Failed to retrieve event [%s] for info request", eventId), e);
				sendMessage(sender, "Got an error trying to retrieve requested event");
				return;
			} finally {
				if (entityManager != null)
					entityManager.close();
			}

			return;
		}

		if (message.matches(eventsCmdPattern)) {
			EntityManager entityManager = null;
			final List<String> events = new LinkedList<String>();
			try {
				entityManager = JPA.newEntityManager();
				final List<Event> resultList = entityManager.createQuery("from Event").getResultList();

				if (resultList == null || resultList.size() == 0) {
					sendMessage(sender, "No events found");
				}

				for (Event event : resultList) {
					events.add(String.format("%s [id: %s]\n", event.title, event.id));
				}
			} catch (Exception e) {
				Logger.error("Failed to retrieve all events");
				sendMessage(sender, "Failed to retrieve all events");
				return;
			} finally {
				if (entityManager != null)
					entityManager.close();
			}

			for(String event : events)
				sendMessage(sender, event);
			return;
		}

		if (message.matches(this.helpCmdPattern)) {
			sendHelpMessage(sender);
			return;
		}

		sendMessage(sender, "Unknown command");
		sendHelpMessage(sender);
	}

	private void sendHelpMessage(final String sender) {
		sendMessage(sender, "Available commands:");
		sendMessage(sender, "help: this help message");
		sendMessage(sender, "info <event-id>: participant information for the requested event");
		sendMessage(sender, "events: a list of all available events");
	}

	//
	// Static methods
	private static PircBot getBot() {
		return (PircBot) StaticHolder.refs.get("irc-bot");
	}
	
	public static void send(String message) throws Exception{
		PircBot bot = getBot();
		if(bot == null){
			Logger.info("Auto-init IRC bot for message");
			start();
		}
		bot = getBot();
		if(bot != null)
			bot.sendMessage(Play.configuration.getProperty("irc.channel", "#test"), message);
		else
			Logger.error("Failed to init IRC bot for message");
	}

	public static void start() throws Exception {
		PircBot bot = getBot();
		if(bot != null){
			Logger.info("disconnecting first");
			bot.disconnect();
			Thread.sleep(1000);
			Logger.info("disconnecting done");
		}else{
			bot = new IRCBot();
			StaticHolder.refs.put("irc-bot", bot);
			if(Play.mode == Mode.DEV)
				bot.setVerbose(true);
		}
		bot.setAutoNickChange(true);
		bot.setEncoding("UTF-8");
		bot.connect(Play.configuration.getProperty("irc.server", "irc.lunatech.com"));
		bot.joinChannel(Play.configuration.getProperty("irc.channel", "#test"));
	}
}
