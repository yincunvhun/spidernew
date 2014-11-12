package com.uestc.spider.www;

import java.util.Queue;

public interface FindLinks {

	public Queue<String> findThemeLinks(String themeLink);
	
	public Queue<String> findContentLinks(String themeLink);
}
