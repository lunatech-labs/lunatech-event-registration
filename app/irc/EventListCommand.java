package irc;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import javax.persistence.EntityManager;

import models.Event;

public class EventListCommand extends Command {
	private final static String eventsCmdPattern = "^\\s*events\\s*$";

	public EventListCommand(IRCBot bot) {
		super("events", eventsCmdPattern, true, bot);
	}

	@Override
	protected void run(String message, String sender, 
			Matcher matcher, EntityManager entityManager) {

		final List<String> events = new LinkedList<String>();
		final List<Event> resultList = entityManager.createQuery("from Event").getResultList();

		if (resultList == null || resultList.size() == 0) {
			bot.sendMessage(sender, "No events found");
		}

		for (Event event : resultList) {
			events.add(String.format("%s [id: %s]\n", event.title, event.id));
		}

		for(String event : events)
			bot.sendMessage(sender, event);
	}

}
