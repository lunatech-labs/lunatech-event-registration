package models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import org.hibernate.validator.constraints.NotEmpty;

import play.db.jpa.Model;

@Entity
public class Participant extends Model {
	public String company;
	
	@Column(unique = true)
	@NotEmpty
	public String emailAddress;
	
	@NotEmpty
	public String firstName;

	@NotEmpty
	public String lastName;
	
	public Integer version;
	
	@Lob
	public String comments;
	
	public Date dateCreated;

	@ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
	public Set<Event> events = new HashSet<Event>();

	@JoinTable(name = "participant_tag",
			joinColumns = @JoinColumn(name = "participant_component_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id"))
	@ManyToMany
	public Set<Tag> tags= new HashSet<Tag>();
}
