package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.google.gson.annotations.Expose;

import models.base.BaseModel;
import play.db.jpa.GenericModel;

/**
 * 
 *
 */
@Entity
@Table(name="FOLDER")
public class Folder extends BaseModel {
	
	@Expose
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@Column(name="PARENT_ID")
	public Long parentId;
	
	@Expose
	@Column(name="LABEL2")
	public String label2;
	
	public static final String TYPE_PAGE = "page";
	
	public static final String TYPE_FILE = "file";
	public static final String TYPE_MEDIA = "media";
	public static final String TYPE_ALBUM = "album";
	
	public Folder()
	{

	}

	public Folder(String name, String type) {
		this.name = name;
		this.type = type;
	}

	@Expose
	@Column(name = "NAME")
	public String name;
	
	@Expose
	@Column(name = "TYPE")
	public String type;
	
	@Transient
	public List<Folder> subFolders;
	
	@Column(name = "IDX")
	public Integer index;
	
	
	@Transient
	public List<Filer> filers;

	public static Folder content(Long id2) {
		Folder result = new Folder();
		if(id2 == null)
		{
			result.subFolders = Folder.find("parentId is null").fetch();
			result.filers = Filer.find("folderId is null").fetch();
		}else
		{
			result.subFolders = Folder.find("parentId = ?", id2).fetch();
			result.filers = Filer.find("folderId = ?", id2).fetch();
		}
		
		return result;
	}
	
	
	
}
