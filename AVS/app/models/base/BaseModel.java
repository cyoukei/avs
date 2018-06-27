package models.base;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

import play.db.jpa.GenericModel;

@MappedSuperclass
public class BaseModel extends GenericModel {
	
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAIL = "fail";
	
	@Column(name="ONUPDATE")
	public Integer onupdate;
	
	@Column(name="STATUS")
	public Integer status;
	
	@Column(name="CREATE_DATE")
	public Date createDate;
	
	@Column(name="UPDATE_DATE")
	public Date updateDate;
	
	@Column(name="URI", length=3000)
	public String uri;
	
	@Column(name="PATH", length=3000)
	public String path;
	
	@PreUpdate
	private void prepareUpdate() {
		updateDate = new Date();
	}
	
	@PrePersist
	private void preparePersist() {
		createDate = updateDate = new Date();
	}
	
}
