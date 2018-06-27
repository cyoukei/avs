package utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class URLUtil {
	
	public static String removeParam(String url, String name)
	{
		if(url.indexOf("?") == -1)
		{
			return url + "?" + name + "=";
		}
		
		List<NameValuePair> params = parse(url.substring(url.indexOf("?") + 1));
		List<NameValuePair> newParams = new LinkedList<NameValuePair>();
		
		for (NameValuePair pair : params) {
			if(!pair.getName().equals(name))
			{
				newParams.add(pair);
			}
		}
		
		String query = format(newParams);
		String hostPath = url.substring(0, url.indexOf("?") + 1);
		if(StringUtils.isBlank(query))
		{
			return hostPath + name + "=";
		}
		
		return hostPath + query + "&" + name + "=";
		
	}
	
	public static String format(List<NameValuePair> params)
	{
		return URLEncodedUtils.format(params, "utf-8");
	}
	
	public static List<NameValuePair> parse(String url) {
		return URLEncodedUtils.parse(url, Charset.forName("utf-8"));
	}
}
