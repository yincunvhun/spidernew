package com.uestc.spider.www;

import java.util.LinkedList;
import java.util.Queue;

/*
 * 对每一个门户网站顶一个特有的类
 * 每个类中针对每个主题新闻有不同的方法获取其新闻
 * 
 * 获取每个新闻的评论（新）
 * 新闻评论links和新闻links也有联系的（评论不需要全部抓下来）
 *
 * */
class NETEASENews implements FindLinks{
	
	//保存获取到的主题links
	public Queue<String> newsThemeLinks = new LinkedList<String>() ;
	
	//保存获取到的新闻links
	public Queue<String> newsContentLinks = new LinkedList<String>() ;
	
	
	//保存已经访问的新闻links 以免新闻重复
	public Queue<String> linksVisited = new LinkedList<String>() ;
	
	//新闻主题link
	private String theme ;
	
	public NETEASENews(String theme){
		
		this.theme = theme ;
	}
	//获取国内新闻
	public void getGuoNeiNews(){
		
		//新闻主题links的正则表达式
		String newsThemeLinksReg = "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
		
		//新闻内容links的正则表达式
		String newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
		
		//保存国内新闻主题links
		Queue<String> guoNeiTheme = new LinkedList<String>();
		guoNeiTheme.offer("http://news.163.com/domestic/");
		for(int i = 2 ; i < 11 ; i++){
			if(i < 10)
				guoNeiTheme.offer("http://news.163.com/special/0001124J/guoneinews_0"+i+".html#headList");
			else
				guoNeiTheme.offer("http://news.163.com/special/0001124J/guoneinews_"+i+".html#headList");
			
		}
//		System.out.println(guoNeiTheme);

	}
	@Override
	public Queue<String> findThemeLinks(String themeLink) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue<String> findContentLinks(String themeLink) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewTitle(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewOriginalTite(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewContent(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewImages(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewTime(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewSource() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewOriginalSource() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewOriginalCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String findNewComment(String url) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}
	
	
}

class SINANews implements FindLinks{

	@Override
	public Queue<String> findThemeLinks(String themeLink) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue<String> findContentLinks(String themeLink) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewTitle(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewOriginalTite(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewContent(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewImages(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewTime(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewOriginalSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewOriginalCategroy(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewComment(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}
	
}

public class GetNewsFromPortals {

	public static void main(String[] args){
		NETEASENews test = new NETEASENews("");
		test.getGuoNeiNews();
	}
	
}


