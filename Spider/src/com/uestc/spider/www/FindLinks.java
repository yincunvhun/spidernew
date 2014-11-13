package com.uestc.spider.www;

import java.util.Queue;

/*
 * 声明各种方法 
 * */
public interface FindLinks {

	public Queue<String> findThemeLinks(String themeLink) ; //获取主题链接
	
	public Queue<String> findContentLinks(String themeLink) ;  //获取内容链接
	
	public String findNewTitle(String html) ; //获取新闻标题
	
	public String findNewOriginalTite(String html) ; //获取新闻原始标题
	
	public String findNewContent(String html) ;    //获取新闻内容
	
	public String findNewImages(String html);     //获取新闻图片
	 
	public String findNewTime(String html) ;        //获取新闻发布时间
	
	public String findNewSource() ;           //新闻来源
	
	public String findNewOriginalSource() ;     //新闻具体来源
	 
	public String findNewCategroy(String html) ;  //新闻版面属性
	
	public String findNewOriginalCategroy(String html); //新闻具体版面属性
	
	public String findNewComment(String url) ;         //获取新闻评论
	
	public void handle();                              //把所有标签加入数据库
	
	
	
}
