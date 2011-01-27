package irc;

import java.util.List;
import java.util.regex.Matcher;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.Event;
import play.Logger;

public class InfoCommand extends Command {
	private final static String infoCmdPattern = "^\\s*info\\s+(\\S+)\\s*$";

	public InfoCommand(IRCBot ircBot) {
		super("info", infoCmdPattern, true, ircBot);
	}

	@Override
	protected void run(String message, String sender, 
			Matcher matcher, EntityManager entityManager) {
		final String eventId = matcher.group(1);

		Event event;
		final Query query = entityManager.createQuery("from Event where id = ?", Event.class);
		query.setParameter(1, eventId);
		final List<Event> resultList = query.getResultList();
		if (resultList.size() == 0) {
			bot.sendMessage(sender, String.format("No event found with id [%s]", eventId));
			return;
		}

		if (resultList.size() > 1) {
			Logger.error("Found more than 1 event with id [%s]");
			bot.sendMessage(sender, String.format("More than 1 event found with id [%s], something is broken", eventId));
			return;
		}

		event = (Event) resultList.get(0);

		bot.sendMessage(sender, String.format("Event [%s] has [%d] registered participants", event.title,
				event.participants.size()));
	}

}
