package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

import models.base.BaseModel;

@Entity
@Table(name="TAG")
public class Tag extends BaseModel {
	@Expose
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@Expose
	@Column(name = "NAME")
	public String name;
	
	
}
