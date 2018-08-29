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
import play.jobs.Every;
import play.jobs.Job;

//@Every("10s")
public class VideoConvertJob extends Job {
	String base = Play.configuration.getProperty("video.location");
	String[] extentions = Play.configuration.getProperty("video.unused.extentions").split(",");
	String ffmpeg = Play.configuration.getProperty("ffmpeg");
	
	@Override
	public void doJob() throws Exception {
		super.doJob();
		Logger.info(">>>VideoConvertJob Starting.");
		
		File file = parseFile(new File(base), Arrays.asList(extentions));
		if(file != null)
		{
			startConvert(file);
		}
		
		Logger.info(">>>VideoConvertJob Completed.");
	}
	
	private File parseFile(File f, List<String> extentions) throws UnsupportedEncodingException
	{
//		Logger.info("parseFile:" + f.getAbsolutePath());
		File ret = null;
		if(!f.exists())
			return ret;
		
		File[] children = f.listFiles();
		if(children == null)
			return ret;
		
		for (File file : children) {
			if(file.isDirectory())
			{
				
				ret = parseFile(file, extentions);
			}
			
			if(ret != null)
			{
				return ret;
			}
			
			String ext = getExtention(file).toLowerCase().trim();
			if(extentions.indexOf(ext) != -1)
			{
				File toDel = new File(file.getAbsolutePath() + ".jpg");
				if(toDel.exists())
				{
					toDel.delete();
				}
				
				toDel = new File(file.getAbsolutePath() + ".png");
				if(toDel.exists())
				{
					toDel.delete();
				}
				
				
				File target = new File(file.getAbsolutePath() + ".mp4");
				if(!target.exists())
				{
					return file;
				}
				
				file.delete();
			}
		}
		
		return ret;
	}
	
	private void startConvert(File file) {
		String out = file.getName() + ".mp4";
		if(new File(file.getParent(), out).exists())
		{
			return;
		}
		
		Logger.info(">>>Converting: " + file.getAbsolutePath());
		
		ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(ffmpeg, "-i", file.getName(), "-c:v", "libx264", 
				"-strict", "-2", out));
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
		if(file.isDirectory())
		{
			return "";
		}
		String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return suffix;
	}
}
