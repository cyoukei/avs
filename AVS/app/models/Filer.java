package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import models.base.BaseModel;

@Entity
@Table(name="FILER")
public class Filer extends BaseModel {
	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_AUDIO = "audio";
	
	@Expose
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@Column(name="NAME")
	public String name;
	
	@Column(name="FOLDER_ID")
	public Long folderId;
	
	@Column(name="LAST_MODIFIED")
	public Date lastModified;
	
	@Column(name="USED_SPACE")
	public Long usedSpace;
	
	@Column(name="TAGS")
	public String tags;
	
	/**
	 * 类型，视频还是音频
	 */
	@Column(name="TYPE")
	public String type;
	
	@Column(name="MEDIA_IDX")
	public Integer mediaIdx;
	
	@Transient
	public String videoUrl;
	
}
