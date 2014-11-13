package com.uestc.spider.www;

import java.util.Queue;

/*
 * 声明各种方法 
 * */
public interface FindLinks {

	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) ; //获取主题链接
	
	public Queue<String> findContentLinks(Queue<String> themeLink ,String ContentLinkReg) ;  //获取内容链接
	
	public String findNewsTitle(String html) ; //获取新闻标题
	
	public String findNewsOriginalTite(String html) ; //获取新闻原始标题
	
	public String findNewsContent(String html) ;    //获取新闻内容
	
	public String findNewsImages(String html);     //获取新闻图片
	 
	public String findNewsTime(String html) ;        //获取新闻发布时间
	
	public String findNewsSource() ;           //新闻来源
	
	public String findNewsOriginalSource() ;     //新闻具体来源
	 
	public String findNewsCategroy(String html) ;  //新闻版面属性
	
	public String findNewsOriginalCategroy(String html); //新闻具体版面属性
	
	public String findNewsComment(String url) ;         //获取新闻评论
	
	public void handle();                              //把所有标签加入数据库
	
	
	
}
