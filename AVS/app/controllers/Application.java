package controllers;

import play.*;
import play.mvc.*;

import java.io.File;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import job.BuildCacheJob;
import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }
    
    public static void build() {
    	new BuildCacheJob().now();
    	
        renderText("Build Started.");
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
    
    public static void date(Integer pageNo)
    {
    	if(pageNo == null)
		{
			pageNo = 1;
		}
    	
    	Page page = new Page();
		page.pageNo = pageNo;
		page.totalCount = Filer.count();
		
		List<Filer> filers = Filer.find("order by lastModified desc").fetch(page.pageNo, page.pageSize);
    	
    	render(filers, page);
    }
    
    public static void name()
    {
    	render();
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
    	}
    	render(filer, parents);
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