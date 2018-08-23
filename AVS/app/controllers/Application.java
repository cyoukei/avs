package controllers;

import play.*;
import play.mvc.*;

import java.io.File;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import job.BuildCacheJob;
import job.VideoConvertJob;
import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
    @Before
	static void initExtraTabs()
	{
    	List<Tag> tags = Tag.all().fetch();
    	renderArgs.put("tags", tags);
	}
    
    public static void build() {
    	new BuildCacheJob().now();
    	
        renderText("Build Started.");
    }
    
    public static void convert() {
    	new VideoConvertJob().now();
    	
        renderText("Convert Started.");
    }
    
    public static void folder(Long id)
    {
    	Folder folder = Folder.content(id);
    	
    	List<Folder> parents = new ArrayList<>();
    	
    	if(id != null)
    	{
    		Folder f = Folder.findById(id);
    		if(f != null)
    		{
    			parents.add(f);
        		while(f.parentId != null)
            	{
            		parents.add(0, (Folder)Folder.findById(f.parentId));
            		f = parents.get(0);
            	}
    		}
    	}
    	
    	
    	
    	
    	render(folder, parents);
    }
    
    public static void date(Integer pageNo, String name)
    {
    	if(pageNo == null)
		{
			pageNo = 1;
		}
    	
    	if(name == null) name = "";
    	
    	Page page = new Page();
		page.pageNo = pageNo;
		page.totalCount = Filer.count("name like ? or description like ? ", "%" + name + "%", "%" + name + "%");
		
		List<Filer> filers = Filer.find("name like ?  or description like ? order by lastModified desc", "%" + name + "%", "%" + name + "%").fetch(page.pageNo, page.pageSize);
    	
		Integer tabIndex = 1;
		
    	render(filers, page, tabIndex, name);
    }
    
    public static void tag(String name, Integer pageNo)
    {
    	
    	if(pageNo == null)
		{
			pageNo = 1;
		}
    	
    	Page page = new Page();
		page.pageNo = pageNo;
		
		Long selectTagId = -1l;
		List<Filer> filers = null;
		
		if("blank".equals(name))
		{
			page.totalCount = Filer.count("tags is null or tags = ''");
			
			filers = Filer.find("tags is null or tags = '' order by lastModified desc").fetch(page.pageNo, page.pageSize);
	    	
		}else
		{
			page.totalCount = Filer.count("tags like ?", "%" + name + "%");
			
			filers = Filer.find("tags like ? order by lastModified desc", "%" + name + "%").fetch(page.pageNo, page.pageSize);
	    	
			Tag selected = Tag.find("name = ?", name).first();
			selectTagId = selected.id;
		}
		
		
		
		
    	render(filers, page, selectTagId);
    }
    
    static String nginx = Play.configuration.getProperty("nginx");
    public static void detail(Long id)
    {
    	
    	Filer filer = Filer.findById(id);
    	filer.videoUrl = nginx + filer.uri;
    	
    	List<Folder> parents = new ArrayList<>();
    	
    	if(filer != null)
    	{
    		Folder f = Folder.findById(filer.folderId);
    		if(f != null)
    		{
    			parents.add(f);
        		while(f.parentId != null)
            	{
            		parents.add(0, (Folder)Folder.findById(f.parentId));
            		f = parents.get(0);
            	}
    		}
    		
    		if(StringUtils.isBlank(filer.tags))
    			filer.tags = null;
    	}
    	
    	
    	render(filer, parents);
    }
    
    public static void removeTag(Long id, String tag)
    {
    	Filer filer = Filer.findById(id);
    	if(filer != null)
    	{
    		String tagStr = filer.tags;
        	if(!StringUtils.isBlank(tagStr))
        	{
        		String[] tags = filer.tags.split(",");
        		List<String> newTags = new ArrayList<>();
        		for (String string : tags) {
    				if(!tag.equals(string))
    				{
    					newTags.add(string);
    				}
    			}
        		
        		filer.tags = StringUtils.join(newTags, ",");
        		filer.save();
        	}
    	}
    	
    	renderText("ok");
    }
    
    public static void addTag(Long id, List<String> tagRadios, String newTags)
    {
    	List<String> tags = new ArrayList<String>();
    	if(tagRadios != null)
    	{
    		for (String t : tagRadios) {
    			if(StringUtils.isBlank(t))
    			{
    				continue;
    			}
    			
    			if("custom".equals(t))
    	    	{
    				if(!StringUtils.isBlank(newTags))
    	    		{
    					newTags = newTags.trim();
    		    		
    					Tag tag = Tag.find("name = ?", newTags).first();
    		    		if(tag == null)
    		    		{
    		    			tag = new Tag();
    		    			tag.name = newTags;
    		    			tag.save();
    		    		}
    		    		
    					tags.add(newTags);
    	    		}
    	    	}else
    	    	{
    	    		tags.add(t);
    	    	}
    		}
    	}
    	
    	
    	Filer filer = Filer.findById(id);
    	filer.tags = StringUtils.join(tags, ",");
    	
    	filer.save();
    	
    	detail(id);
    }
    
    public static void updateDescription(Long id, String description)
    {
    	Filer filer = Filer.findById(id);
    	filer.description = description;
    	filer.save();
    	
    	detail(id);
    }
    
    
    static String base = Play.configuration.getProperty("video.location");
    public static void image(Long id)
    {
    	Filer filer = Filer.findById(id);
		if(filer == null)
		{
			notFound("Can't find filer:" + id);
		}
		
		File f = new File(base + filer.path + ".jpg");
		if(!f.exists())
		{
			notFound("Can't find file:" + f.getAbsolutePath());
		}
		
		renderBinary(f);
    }

}