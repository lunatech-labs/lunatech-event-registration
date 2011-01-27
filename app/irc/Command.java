package irc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import play.Logger;
import play.db.jpa.JPA;

public abstract class Command {
	public String name;
	public Pattern pattern;
	public boolean wantsEntityManager;
	public IRCBot bot;

	public Command(String name, String pattern, boolean wantsEntityManager, IRCBot ircBot) {
		this.name = name;
		this.pattern = Pattern.compile(pattern);
		this.wantsEntityManager = wantsEntityManager;
		this.bot = ircBot;
	}
	
	protected abstract void run(String message, String sender, Matcher matcher, EntityManager entityManager);
	
	public void runCommand(String message, String sender, Matcher matcher){
		EntityManager entityManager = null;
		try {
			if(wantsEntityManager)
				entityManager = JPA.newEntityManager();
			run(message, sender, matcher, entityManager);
		} catch (Exception e) {
			Logger.error(e, "Failed to execute command [%s]", message);
			bot.sendMessage(sender, "Failed to execute command");
		} finally {
			if (entityManager != null)
				entityManager.close();
		}
	}

	public Matcher getMatcher(String message) {
		return pattern.matcher(message);
	}
}
