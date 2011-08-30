package controllers;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Path.Node;

import models.Community;
import models.Event;
import models.Participant;
import models.Tag;
import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Application extends Controller {

    public static void index() {
    	List<Event> events = Event.find("order by date desc").fetch();
        render(events);
    }

    public static void event(String id){
    	Event event = Event.findById(id);
    	notFoundIfNull(event);

    	render(event);
    }

    public static void createEvent(){
    	Event event = new Event();
    	render("Application/editEvent.html", event);
    }

    public static void editEvent(String id){
    	Event event = Event.findById(id);
    	notFoundIfNull(event);

    	render(event);
    }

    public static void deleteEvent(String id){
    	Event event = Event.findById(id);
    	notFoundIfNull(event);

    	event.delete();
    	index();
    }

    public static void saveEvent(Event event){
    	boolean wasPersistent = event.isPersistent();
    	if(!wasPersistent){
        	event.dateCreated = new Date();
        	event.template = "layout/lunatech.xhtml";
        	event.community = Community.findById("lunatech-labs");
    	}
    	try{
    		event.save();
    	}catch(ConstraintViolationException x){
    		// silly bug, where a failed save will mark it as persistent
    		if(!wasPersistent)
    			JPA.em().detach(event);
    		Set<ConstraintViolation<?>> violations = x.getConstraintViolations();
    		for(ConstraintViolation<?> violation : violations){
    			String message = violation.getMessage();
    			Path path = violation.getPropertyPath();
    			String p = "event";
    			for (Node node : path){
    				p += ".";
    				p += node.getName();
    			}
    			Validation.addError(p, message);
    		}
    		render("Application/editEvent.html", event);
    	}
    	event(event.id);
    }

    public static void unregisterParticipant(String eventId, Long userId){
    	Event event = Event.findById(eventId);
    	notFoundIfNull(event);
    	Participant participant = Participant.findById(userId);
    	notFoundIfNull(participant);

    	event.participants.remove(participant);
    	event.save();
    	event(eventId);
    }

    public static void removeTag(String eventId, Long tagId){
    	Event event = Event.findById(eventId);
    	notFoundIfNull(event);
    	Tag tag = Tag.findById(tagId);
    	notFoundIfNull(tag);

    	event.tags.remove(tag);
    	event.save();
    	event(eventId);
    }

    public static void addTags(String eventId, String tags){
    	Event event = Event.findById(eventId);
    	notFoundIfNull(event);

    	for(String tag : tags.split("\\s+")){
    		if(tag.isEmpty())
    			continue;
    		Tag newTag = Tag.find("lower(name) = lower(?)", tag).first();
    		if(newTag == null){
    			newTag = new Tag();
    			newTag.name = tag;
    			newTag.save();
    		}
        	event.tags.add(newTag);
    	}
    	event.save();
    	event(eventId);
    }

    public static void removeParticipantTag(Long participantId, Long tagId){
    	Participant participant = Participant.findById(participantId);
    	notFoundIfNull(participant);
    	Tag tag = Tag.findById(tagId);
    	notFoundIfNull(tag);

    	participant.tags.remove(tag);
    	participant.save();
    	participant(participantId);
    }

    public static void addParticipantTags(Long participantId, String tags){
    	Participant participant = Participant.findById(participantId);
    	notFoundIfNull(participant);

    	for(String tag : tags.split("\\s+")){
    		if(tag.isEmpty())
    			continue;
    		Tag newTag = Tag.find("lower(name) = lower(?)", tag).first();
    		if(newTag == null){
    			newTag = new Tag();
    			newTag.name = tag;
    			newTag.save();
    		}
        	participant.tags.add(newTag);
    	}
    	participant.save();
    	participant(participantId);
    }

    public static void participants() {
    	List<Participant> participants = Participant.find("ORDER BY firstName, lastName").fetch();
        render(participants);
    }

    public static void participant(Long participantId){
    	Participant participant = Participant.findById(participantId);
    	notFoundIfNull(participant);

    	render(participant);
    }

    public static void deleteParticipant(Long id){
    	Participant participant = Participant.findById(id);
    	notFoundIfNull(participant);

    	for(Event event : participant.events){
    		event.participants.remove(participant);
    		event.save();
    	}
    	participant.delete();
    	index();
    }
}
