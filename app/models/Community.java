package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "registration_community")
public class Community extends GenericModel {
	@Id
	public String id;
	
	public String name;
	
	@Lob
	public String description;
	
	@Lob
	public byte[] logo;
	
	@Lob
	public String signature;
	
	public String contactEmail;

	@Lob
	public String css;
	
	@JoinTable(name = "registration_community_registration_administrator",
			joinColumns = @JoinColumn(name = "communities_id"),
			inverseJoinColumns = @JoinColumn(name = "administrators_emailaddress"))
	@ManyToMany
	public Set<Administrator> administrators = new HashSet<Administrator>();

}
