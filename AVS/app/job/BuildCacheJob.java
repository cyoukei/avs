package job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.codehaus.groovy.ast.expr.FieldExpression;

import models.Filer;
import models.Folder;
import play.Logger;
import play.Play;
import play.jobs.Job;

public class BuildCacheJob extends Job {
	String base = Play.configuration.getProperty("video.location");
	String[] extentions = Play.configuration.getProperty("video.extentions").split(",");
	String ffmpeg = Play.configuration.getProperty("ffmpeg");
	
	@Override
	public void doJob() throws Exception {
		super.doJob();
		
		
		
		Folder.em().createQuery("update Folder set onupdate = 0").executeUpdate();
		Filer.em().createQuery("update Filer set onupdate = 0").executeUpdate();
		
		
		parseFile(new File(base), null, Arrays.asList(extentions), "", "");
		
		Folder.delete("from Folder where onupdate = 0");
		Filer.delete("from Filer where onupdate = 0");
		
		Logger.info(">>>BuildCacheJob Completed.");
	}
	
	private void parseFile(File f, Long parentId, List<String> extentions, String uri, String path) throws UnsupportedEncodingException
	{
		Logger.info(">>>parseFile:" + path + "/" + f.getName());
		if(!f.exists())
			return;
		
		String newUri = uri + "/" + URLEncoder.encode(f.getName(), "utf-8");
		
		
		Folder folder = Folder.find("uri = ?", newUri).first();
		if(folder == null)
		{
			folder = new Folder();
			folder.name = f.getName();
			folder.parentId = parentId;
			folder.uri = newUri;
			folder.path = path + "/" + f.getName();
		}
		
		folder.onupdate = 1;
		folder.save();
		
		
		File[] children = f.listFiles();
		if(children == null)
			return;
		
		for (File file : children) {
			if(file.isDirectory())
			{
				parseFile(file, folder.id, extentions, folder.uri, folder.path);
			}else
			{
				String ext = getExtention(file).toLowerCase().trim();
				if(extentions.indexOf(ext) != -1)
				{
					String fileUri =  folder.uri + "/" + URLEncoder.encode(file.getName(), "utf-8");
					Filer filer = Filer.find("uri = ?", fileUri).first();
					if(filer == null)
					{
						filer = new Filer();
						filer.name = file.getName();
						filer.type = ext;
						filer.folderId = folder.id;
						filer.lastModified =  new Date(file.lastModified());
						filer.usedSpace = file.length();
						filer.uri = fileUri;
						filer.path = folder.path + "/" + file.getName();
					}
					
					filer.onupdate = 1;
					filer.save();
					
					checkToGenThumbnail(file);
				}
			}
		}
	}
	
	private void checkToGenThumbnail(File file) {
		String out = file.getName() + ".jpg";
		if(new File(file.getParent(), out).exists())
		{
			return;
		}
		
		Logger.debug(">>>Parsing " + file.getName());
		
		ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(ffmpeg, "-i", file.getName(), "-ss", "00:01:26", 
				"-y", "-t", "1", "-f", "image2", "-r", "1",  "-vf", "scale=320:-2", out));
		processBuilder.directory(file.getParentFile());
		processBuilder.redirectErrorStream(true);
		Process process;
		try {
			process = processBuilder.start();
			
			byte[] b = new byte[1024];
			int readbytes = -1;
			InputStream in = process.getInputStream();
			try {
				while ((readbytes = in.read(b)) != -1) {
				}
			} catch (IOException e1) {
			} finally {
				try {
					in.close();
				} catch (IOException e2) {
				}
			}
			process.waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private String getExtention(File file)
	{
		String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return suffix;
	}
}
