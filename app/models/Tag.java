package models;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
@AttributeOverride(name = "id", column = @Column(name = "tag_id"))
public class Tag extends Model {
	public String name;
}
