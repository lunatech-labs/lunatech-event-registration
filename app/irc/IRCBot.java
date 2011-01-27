package irc;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import org.jibble.pircbot.PircBot;

import play.Logger;
import play.Play;
import play.Play.Mode;

import com.lunatech.events.StaticHolder;

public class IRCBot extends PircBot {

	public List<Command> commands = new LinkedList<Command>();
	
	private IRCBot(){
		setName(Play.configuration.getProperty("irc.nick", "EventBot"));
		commands.add(new EventListCommand(this));
		commands.add(new HelpCommand(this));
		commands.add(new InfoCommand(this));
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
		for(Command cmd : commands){
			Matcher matcher = cmd.getMatcher(message);
			if(matcher.matches()){
				cmd.runCommand(message, sender, matcher);
				return;
			}
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
