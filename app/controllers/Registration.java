package controllers;

import java.util.HashMap;
import java.util.Map;

import jobs.IRCMessageJob;

import org.apache.commons.lang.StringUtils;

import mails.RegistrationMails;
import models.Event;
import models.Participant;
import play.data.validation.Validation;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Router;

public class Registration extends Controller {
	public static void embed(String community, String eventId){
		Event event = Event.findById(eventId);
		notFoundIfNull(event);
		
		render("Registration/embed.js", community, event);
	}

	public static void register(String community, String eventId, boolean embed, String returnURL){
		Event event = Event.findById(eventId);
		notFoundIfNull(event);
		
		render(community, event, embed, returnURL);
	}

	public static void registerParticipant(String community, String eventId, boolean embed, String returnURL, 
			Participant participant){
		Event event = Event.findById(eventId);
		notFoundIfNull(event);

		Validation.required("participant.emailAddress", participant.emailAddress);
		Validation.email("participant.emailAddress", participant.emailAddress);
		if(Validation.hasErrors()){
			params.flash();
			Validation.keep();
			register(community, eventId, embed, returnURL);
		}
		
		String emailAddress = participant.emailAddress;
		String confirmationCode = Crypto.sign(participant.emailAddress);

		Map<String, Object> args = new HashMap<String, Object>();
		args.put("confirmationCode", confirmationCode);
		args.put("emailAddress", emailAddress);
		args.put("community", community);
		args.put("eventId", eventId);
		String confirmURL = Router.getFullUrl("Registration.confirm", args);
		
		if(!StringUtils.isEmpty(returnURL)){
			returnURL += returnURL.contains("?") ? "&" : "?";
			returnURL += "confirmationCode="+confirmationCode+"&emailAddress="+emailAddress;
		}else
			returnURL = confirmURL;
		
		RegistrationMails.confirmEmail(emailAddress, event, returnURL);
		render(community, event, confirmURL, embed, returnURL);
	}
	
	public static void confirm(String community, String eventId, String emailAddress, String confirmationCode,
			boolean embed){
		Event event = Event.findById(eventId);
		notFoundIfNull(event);

		if(!Crypto.sign(emailAddress).equals(confirmationCode)){
			String error = "Invalid confirmation code";
			error(error, embed);
		}
		
		Participant existingParticipant = Participant.find("lower(emailAddress) = lower(?)", emailAddress).first();
		if(existingParticipant != null){
			if(event.participants.contains(existingParticipant)){
				String error = "You are already registered for this event";
				error(error, embed);
			}
			flash.put("participant.firstName", existingParticipant.firstName);
			flash.put("participant.lastName", existingParticipant.lastName);
			flash.put("participant.company", existingParticipant.company);
		}
		
		render(community, event, confirmationCode, emailAddress, embed);
	}
	
	public static void confirmParticipant(String community, String eventId, String confirmationCode, 
			boolean embed, Participant participant){
		Event event = Event.findById(eventId);
		notFoundIfNull(event);

		Validation.required("participant.emailAddress", participant.emailAddress);
		Validation.required("participant.firstName", participant.firstName);
		Validation.required("participant.lastName", participant.lastName);
		Validation.required("participant.company", participant.company);
		if(Validation.hasErrors()){
			params.flash();
			Validation.keep();
			confirm(community, eventId, participant.emailAddress, confirmationCode, embed);
		}

		if(!event.canStillRegister()){
			String error = "Event is closed, try to register early next time ;)";
			error(error, embed);
		}
		
		// we must check this again here
		if(!Crypto.sign(participant.emailAddress).equals(confirmationCode)){
			String error = "Invalid confirmation code";
			error(error, embed);
		}

		Participant existingParticipant = Participant.find("lower(emailAddress) = lower(?)", participant.emailAddress).first();
		if(existingParticipant != null){
			if(event.participants.contains(existingParticipant)){
				String error = "You are already registered for this event";
				error(error, embed);
			}
			existingParticipant.firstName = participant.firstName;
			existingParticipant.lastName = participant.lastName;
			existingParticipant.company = participant.company;
			existingParticipant.comments = participant.comments;
			participant = existingParticipant;
		}
		participant.events.add(event);
		participant.tags.addAll(event.tags);
		participant.save();
		event.participants.add(participant);
		event.save();
		
		IRCMessageJob ircMessageJob = new IRCMessageJob(participant.firstName+" "+participant.lastName
				+" ("+participant.emailAddress
				+"), from "+participant.company+", has registered for "+event.title+", which now has "
				+event.participants.size()+" participant(s)");
		ircMessageJob.now();
		
		render(event, embed);
	}

	public static void error(String message, boolean embed){
		render(message, embed);
	}
	
	public static void test(String community, String eventId){
		render(community, eventId);
	}
}
