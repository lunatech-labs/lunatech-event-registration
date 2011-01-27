package irc;

import java.util.regex.Matcher;

import javax.persistence.EntityManager;

public class HelpCommand extends Command {

	private final static String helpCmdPattern = "^\\s*help\\s*$";

	public HelpCommand(IRCBot bot) {
		super("help", helpCmdPattern, false, bot);
	}

	@Override
	protected void run(String message, String sender, 
			Matcher matcher, EntityManager entityManager) {
		bot.sendMessage(sender, "Available commands:");
		bot.sendMessage(sender, "help: this help message");
		bot.sendMessage(sender, "info <event-id>: participant information for the requested event");
		bot.sendMessage(sender, "events: a list of all available events");
	}

}
