package models;

import java.util.ArrayList;
import java.util.List;

import play.Logger;

public class Page {
	/**
	 * 当前显示的页，从1开始
	 */
	public int pageNo = 1;
	
	/**
	 * 每页显示数目
	 */
	public int pageSize = 24;
	
	/**
	 * 总条数
	 */
	public long totalCount = 0;
	
	/**
	 * 总页数，最小为1
	 */
	public long getTotalPage()
	{
		long total = Math.round(Math.ceil(1d * totalCount / pageSize));
		if(total == 0)
		{
			total++;
		}
		
		return total;
	}
	
	public List<Long> getShows()
	{
		List<Long> shows = new ArrayList<Long>();
		long total = getTotalPage();
		
		long start = pageNo - 2;
		long end = pageNo + 2;
		if(start < 1)
		{
			start = 1;
		}
		
		if(end > total)
		{
			end = total;
		}
		
		for(long i = start; i <= end; i++)
		{
			shows.add(i);
		}
		
		return shows;
	}
}
