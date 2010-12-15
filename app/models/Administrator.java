package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "registration_administrator")
public class Administrator extends GenericModel {
	
	@Id
	public String emailAddress;
	
	@NotEmpty
	public String firstName;

	@NotEmpty
	public String lastName;
	
	@NotEmpty
	public String password;

	@ManyToMany
	public Set<Community> communities = new HashSet<Community>();
}
