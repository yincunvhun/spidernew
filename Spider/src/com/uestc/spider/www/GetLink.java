package com.uestc.spider.www;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
/*
 * 网速貌似是瓶颈 不存在内存不足现象
 * 2014.10.21需要修改的地方：1. 无法访问的链接保存下来
 *                       2.访问超时的链接保存下来 等到合适的时间继续访问
 *                       3.
 * */
public class GetLink {
	
//	public String url;
	//上一期，为了扩展
	public Queue<String> linkLast = new LinkedList<String>();
	//下一期 扩展
	public Queue<String> linkNext = new LinkedList<String>();
	//保存主题链接
	public Queue<String> linkTheme = new LinkedList<String>();
	//pdf 扩展
	public Queue<String> linkPdf = new LinkedList<String>();
	//保存每个主题内容的链接
	public Queue<String> linkContent = new LinkedList<String>();
	//保存已经访问过的链接
	public Queue<String> linkVisit = new LinkedList<String>();
	
	//匹配link的正则表达式
//	private String zzContent ="http://www.wccdaily.com.cn/shtml/hxdsb/20141024/[0-9]{6}.shtml";
	
//	public GetLink(String url){
//		this.url = url;
//	}
	//该url参数必须是某一板块的themeurl 不能使某一个具体新闻的themeurl
	public void getLink(String themeUrl){
		int state ;
		try{
			HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(themeUrl).openConnection(); //创建连接
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("网络慢，已经无法正常链接，无法获取新闻");
			return;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("网络超级慢，已经无法正常链接，无法获取新闻");
			return ;
      }
		if(state != 200 && state != 201){
			return;
		}
		try{
			Parser parser = new Parser(themeUrl);
			parser.setEncoding("utf-8");
			NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
				public boolean accept(Node node)
				{
					if (node instanceof LinkTag)// 标记
						return true;
					return false;
				}
			
			});
		
			//
			//新闻板块的正则表达式
			Pattern newPage = Pattern.compile("http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm");
			//新闻内容的正则表达式
			Pattern newContent = Pattern.compile("http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm"); //"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm");
			//PDF正则表达式
			Pattern newPdf = Pattern.compile("http://e.chengdu.cn/page/[0-9]{1}/[0-9]{4}-[0-9]{2}/[0-9]{2}/[0-9]{2}/[0-9]{10}_pdf.pdf");
		
		//获取一个网页所有的主题url 内容url pdf url
			for (int i = 0; i < nodeList.size(); i++)
			{
			
				LinkTag n = (LinkTag) nodeList.elementAt(i);
//	        	System.out.print(n.getStringText() + "==>> ");
//	       	 	System.out.println(n.extractLink());
				//某一版
				Matcher themeMatcher = newPage.matcher(n.extractLink());
				//具体的内容
				Matcher contentMatcher = newContent.matcher(n.extractLink());
				//PDF
				Matcher pdfMatcher = newPdf.matcher(n.extractLink());
	        
				if(!linkVisit.contains(n.extractLink())){
	        			if(themeMatcher.find()){
	        				linkTheme.offer(n.extractLink());
	        				linkVisit.offer(n.extractLink());
	        			}
	        			if(contentMatcher.find()){
	        				linkContent.offer(n.extractLink());
	        				linkVisit.offer(n.extractLink());
	        			}
	        			if(pdfMatcher.find()){
	        				linkPdf.offer(n.extractLink());
	        				linkVisit.offer(n.extractLink());
	        			}
	        	
				}
				
				themeMatcher = null;
				contentMatcher = null;
				pdfMatcher = null;
			}
		}catch(ParserException e){
			return ;
		}catch(Exception e){
			return ;
		}
	}
	
	
	public void allWeWillDo(String themeUrl) throws Exception{
		
		int i = 0;
		linkTheme.offer(themeUrl);
//			linkVisit.offer(n.extractLink());
			while(!linkTheme.isEmpty()){
				getLink(linkTheme.poll());
				while(!linkContent.isEmpty()){
					StringBuffer s = new StringBuffer(linkContent.poll());
					i++;
//					System.out.println(s);
					CDSB cdsb = new CDSB(s.toString());
					cdsb.memory(s.toString());
					s = null;
					cdsb = null;
				}
//				System.out.println("我正在把获取的新闻存入数据库...");
			}
			System.out.println("发现的新闻条数："+ i);
			
		
	
	}
	
	
	public void result(int year,int month ,int day) throws Exception{
		StringBuffer s1 = new StringBuffer("http://e.chengdu.cn/html/2014-10");
		StringBuffer s2 = new StringBuffer("/node_2.htm");
		StringBuffer s3 = new StringBuffer("/");
		for(int j  = 1 ; j < 2 ;j ++){
			StringBuffer url = new StringBuffer();
			for(int i = 24 ; i < 25 ;i++){
//				String url;
				if(i < 10)
					url = url.append(s1).append(j).append(s3).append("0").append(i).append(s2);
				else
					url = url.append(s1).append(s3).append(i).append(s2); //url.append(s1).append(j).append(s3).append(i).append(s2);
				
//			System.out.println(url);
				allWeWillDo(url.toString());   
			//清空已经访问的link列表，即每天的新闻爬取存储后要对所有访问过的链接进行清理，节约内存
				linkVisit.clear();
				
			
			}
			url = null;
			System.gc();
		}
	}
	
	public void hxdsb(){
		
		StringBuffer s1 = new StringBuffer("http://www.wccdaily.com.cn/shtml/hxdsb/20141024/va");
		StringBuffer s2 = new StringBuffer(".shtml");
		
		for(int i = 1 ; i < 37 ; i++){
			StringBuffer theme = new StringBuffer();
			if(i < 10)
				theme = theme.append(s1).append(0).append(i).append(s2);
			else
				theme = theme.append(s1).append(i).append(s2);
			System.out.println(theme);
			getLink(theme.toString());
			while(!linkContent.isEmpty()){
				StringBuffer s = new StringBuffer(linkContent.poll());
//				i++;
				System.out.println(s);
				CDSB cdsb = new CDSB(s.toString());
				cdsb.memory(s.toString());
				s = null;
				cdsb = null;
			}
//			theme = null;
		}
		
	}
	
	public static void main(String args[]) throws Exception{
		long start = System.currentTimeMillis();
		String url ="http://www.wccdaily.com.cn/shtml/hxdsb/20141024/va01.shtml" ;
		GetLink test = new GetLink();
//		test.hxdsb();
//		test.getLink(url);
		test.result(0, 0, 0);
//		String url = "http://e.chengdu.cn/html/2014-10/16/node_2.htm";
//		System.out.println(" 我正在努力搜索新闻...");
//		test.allWeWillDo(url);
//		System.out.println("程序执行完毕...");
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
}
