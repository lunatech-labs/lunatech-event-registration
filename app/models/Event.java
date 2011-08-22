package models;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import play.data.binding.As;
import play.db.jpa.GenericModel;

@Entity
public class Event extends GenericModel {
	@Id
	public String id;

	@As("dd-MM-yyyy")
	@NotNull
	public Date date;

	@NotEmpty
	@Lob
	public String description;

	@NotEmpty
	public String template;

	@NotEmpty
	public String title;

	public Integer version;

	public Date dateCreated;

	public boolean open;

	@Lob
	public String descriptionAfterLimit;

	public Boolean hardLimit;

	public Integer participantLimit;

	public Boolean embed;

	@JoinColumn(name = "community_id")
	@ManyToOne
	public Community community;

	@JoinTable(joinColumns = @JoinColumn(name = "events_id"),
			inverseJoinColumns = @JoinColumn(name = "participants_id"))
	@ManyToMany
	public Set<Participant> participants = new HashSet<Participant>();

	@JoinTable(joinColumns = @JoinColumn(name = "event_component_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id"))
	@ManyToMany
	public Set<Tag> tags = new HashSet<Tag>();

	public boolean isFull() {
		return hardLimit && participantLimit != null && participants.size() >= participantLimit;
	}

	public boolean isRegistrationOpen(){
		if(!open)
			return false;
		Calendar dayAfterEvent = new GregorianCalendar();
		dayAfterEvent.setTime(date);
		dayAfterEvent.add(Calendar.DAY_OF_YEAR, 1);
		return new Date().before(dayAfterEvent.getTime());
	}


	public boolean canStillRegister() {
		if(!isRegistrationOpen())
			return false;
		if(participantLimit == null)
			return true;
		return participants.size() < participantLimit;
	}
}
