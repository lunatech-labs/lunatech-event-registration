package mails;

import models.Event;
import play.Play;
import play.mvc.Mailer;

public class RegistrationMails extends Mailer {
	public static void confirmEmail(String emailAddress, Event event, String confirmURL){
		setSubject("Confirm email address to register for "+event.title);
		addRecipient(emailAddress);
		// FIXME: this should come from the event
		setFrom(Play.configuration.getProperty("mail.from", "events@lunatech.com"));
		send(emailAddress, event, confirmURL);
	}
}
