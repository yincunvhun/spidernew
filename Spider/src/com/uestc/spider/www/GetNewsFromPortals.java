package com.uestc.spider.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/*
 * 对每一个门户网站顶一个特有的类
 * 每个类中针对每个主题新闻有不同的方法获取其新闻
 * 
 * 获取每个新闻的评论（新）
 * 新闻评论links和新闻links也有联系的（评论不需要全部抓下来）
 *
 * */
class NETEASENews implements FindLinks{
	
	private String ENCODE ;
	
	private String[] newsTitleLabel;     //新闻标题标签 title or id=h1title
	private String[] newsContentLabel ;  //新闻内容标签 "id","endText"
	private String[] newsTimeLabel ;   //新闻时间"class","ep-time-soure cDGray"
	private String[] newsSourceLabel ; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
	private String[] newsCategroyLabel ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
//	private String[] news
	
	public NETEASENews(String encode){
		this.ENCODE = encode ;
	}
	
	//保存获取到的主题links
	public Queue<String> newsThemeLinks = new LinkedList<String>() ;
	
	//保存获取到的新闻links
	public Queue<String> newsContentLinks = new LinkedList<String>() ;
	
	//保存已经访问的新闻links 以免新闻重复
	public Queue<String> linksVisited = new LinkedList<String>() ;
	
	//新闻主题links的正则表达式
	String newsThemeLinksReg ; //= "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
			
	//新闻内容links的正则表达式
	String newsContentLinksReg ; //= "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
	
	//新闻主题link
	private String theme ;
	
//	public NETEASENews(String theme){
//		
//		this.theme = theme ;
//	}
	//获取国内新闻
	public void getGuoNeiNews(){
		
		//国内新闻 首页链接
		theme = "http://news.163.com/domestic/";
		
		//新闻主题links的正则表达式
		newsThemeLinksReg = "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
		
		//新闻内容links的正则表达式
		newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
		
		int state ;
		try{
			HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(theme).openConnection(); //创建连接
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
		//保存国内新闻主题links
		Queue<String> guoNeiNewsTheme = new LinkedList<String>();
		guoNeiNewsTheme = findThemeLinks(theme,newsThemeLinksReg);
//		System.out.println(guoNeiNewsTheme);
		
		//获取国内新闻内容links
		Queue<String>guoNeiNewsContent = new LinkedList<String>();
		guoNeiNewsContent = findContentLinks(guoNeiNewsTheme,newsContentLinksReg);
//		System.out.println(guoNeiNewsContent);
		//获取每个新闻网页的html
		int i = 0;
		while(!guoNeiNewsContent.isEmpty()){
			String buf = guoNeiNewsContent.poll();
			String html = findContentHtml(buf);  //获取新闻的html
			System.out.println(buf);
//			System.out.println(html);
			i++;
			System.out.println(findNewsComment(buf));
			System.out.println("\n");
		}
		System.out.println(i);
		
		

	}
	@Override
	public Queue<String> findThemeLinks(String themeLink ,String themeLinkReg) {
		
		// TODO Auto-generated method stub
		Queue<String> themelinks = new LinkedList<String>();
		Pattern newsThemeLink = Pattern.compile(themeLinkReg);
		themelinks.offer(themeLink);
		
		try {
				Parser parser = new Parser(themeLink);
				parser.setEncoding(ENCODE);
				NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
					public boolean accept(Node node)
					{
						if (node instanceof LinkTag)// 标记
							return true;
						return false;
					}});
				
				for (int i = 0; i < nodeList.size(); i++)
				{
				
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//		        	System.out.print(n.getStringText() + "==>> ");
//		       	 	System.out.println(n.extractLink());
					//新闻主题
					Matcher themeMatcher = newsThemeLink.matcher(n.extractLink());
					if(themeMatcher.find()){
						if(!themelinks.contains("n.extractLink()"))
							themelinks.offer(n.extractLink());
		        	}
				}
			}catch(ParserException e){
				return null;
			}catch(Exception e){
				return null;
			}
		return themelinks ;
	}

	public Queue<String> findContentLinks(Queue<String> themeLink ,String contentLinkReg) {
		// TODO Auto-generated method stub
		Queue<String> contentlinks = new LinkedList<String>(); // 临时征用
		
		Pattern newsContent = Pattern.compile(contentLinkReg);
		while(!themeLink.isEmpty()){
			
			String buf = themeLink.poll();
		
			try {
				Parser parser = new Parser(buf);
				parser.setEncoding(ENCODE);
				NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
					public boolean accept(Node node)
					{
						if (node instanceof LinkTag)// 标记
							return true;
						return false;
					}
		
				});
			
				for (int i = 0; i < nodeList.size(); i++)
				{
			
					LinkTag n = (LinkTag) nodeList.elementAt(i);
//	        	System.out.print(n.getStringText() + "==>> ");
//	       	 	System.out.println(n.extractLink());
					//新闻主题
					Matcher themeMatcher = newsContent.matcher(n.extractLink());
					if(themeMatcher.find()){
					
						if(!contentlinks.contains(n.extractLink()))
							contentlinks.offer(n.extractLink());
					}
				}
			}catch(ParserException e){
				return null;
			}catch(Exception e){
				return null;
			}		
		}
//		System.out.println(contentlinks);
		return contentlinks;
	}
	
	@Override
	public String findContentHtml(String url) {
		// TODO Auto-generated method stub
		String html = null;                 //网页html
		
		HttpURLConnection httpUrlConnection;
	    InputStream inputStream;
	    BufferedReader bufferedReader;
	    
		int state;
		//判断url是否为有效连接
		try{
			httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
			state = httpUrlConnection.getResponseCode();
			httpUrlConnection.disconnect();
		}catch (MalformedURLException e) {
//          e.printStackTrace();
			System.out.println("该连接"+url+"网络有故障，已经无法正常链接，无法获取新闻");
			return null ;
		} catch (IOException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
			System.out.println("该连接"+url+"网络超级慢，已经无法正常链接，无法获取新闻");
			return null ;
      }
		if(state != 200 && state != 201){
			return null;
		}
  
        try {
        	httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
        	httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setUseCaches(true); //使用缓存
            httpUrlConnection.connect();           //建立连接  链接超时处理
        } catch (IOException e) {
        	System.out.println("该链接访问超时...");
        	return null;
        }
  
        try {
            inputStream = httpUrlConnection.getInputStream(); //读取输入流
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, ENCODE)); 
            String string;
            StringBuffer sb = new StringBuffer();
            while ((string = bufferedReader.readLine()) != null) {
            	sb.append(string);
            	sb.append("\n");
            }
            html = sb.toString();
        } catch (IOException e) {
//            e.printStackTrace();
        }
//        System.out.println(html);
		return html;
	}
	
	@Override
	public String HandleHtml(String html, String one) {
		// TODO Auto-generated method stub
		NodeFilter filter = new HasAttributeFilter(one);
		String buf = "";
		try{
			Parser parser = Parser.createParser(html, ENCODE);
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
   		
			if(nodes!=null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node textnode1 = (Node) nodes.elementAt(i);
					buf += textnode1.toPlainTextString();
					if(buf.contains("&nbsp;"))
						buf = buf.replaceAll("&nbsp;", "\n");
				}
			}
		}catch(Exception e){
		   
		   
		}
		return buf ;
	}
	
	@Override
	public String HandleHtml(String html, String one, String two) {
		// TODO Auto-generated method stub
		NodeFilter filter = new HasAttributeFilter(one,two);
		String buf = "";
		try{
			Parser parser = Parser.createParser(html, ENCODE);
			NodeList nodes = parser.extractAllNodesThatMatch(filter);
   		
			if(nodes!=null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node textnode1 = (Node) nodes.elementAt(i);
					buf += textnode1.toPlainTextString();
					if(buf.contains("&nbsp;"))
						buf = buf.replaceAll("&nbsp;", "\n");
				}
			}
		}catch(Exception e){
 
		}
		return buf ;
	}
	//news title
	public String findNewsTitle(String html , String[] label,String buf) {
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		if(titleBuf.contains(buf))
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(buf))	;
		return titleBuf;
	}
	//news 未处理标题
	public String findNewsOriginalTite(String html , String[] label,String buf) {
		// TODO Auto-generated method stub
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
		if(titleBuf.contains(buf))
			titleBuf = titleBuf.substring(0, titleBuf.indexOf(buf))	;
		return titleBuf;
	}
	@Override
	public String findNewsContent(String html , String[] label) {
		// TODO Auto-generated method stub
		String contentBuf;
		if(label[1].equals("")){
			contentBuf = HandleHtml(html,label[0]);
		}else{
			contentBuf = HandleHtml(html,label[0],label[1]);
		}
		
		return contentBuf;
	}
	@Override
	public String findNewsImages(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}
	//新闻时间
	@Override
	public String findNewsTime(String html , String[] label) {
		// TODO Auto-generated method stub
		String timeBuf;
		if(label[1].equals("")){
			timeBuf = HandleHtml(html , label[0]);
		}else{
			timeBuf = HandleHtml(html , label[0],label[1]);
		}
		timeBuf = timeBuf.substring(9, 28);  //根据不同新闻 不同处理
		return timeBuf;
	}
	@Override
	public String findNewsSource(String html ,String[] label) {
		// TODO Auto-generated method stub
		if(label.length == 3 && (!label[2].equals("")))
			return label[2];
		else
			return "网易新闻-国内新闻";
	}
	@Override
	public String findNewsOriginalSource(String html ,String[] label) {
		// TODO Auto-generated method stub
		String sourceBuf;
		if(label[1].equals("")){
			sourceBuf = HandleHtml(html , label[0]);
		}else{
			sourceBuf = HandleHtml(html , label[0],label[1]);
		}
		
		if(sourceBuf.length() >29)
			sourceBuf = sourceBuf.substring(29, sourceBuf.length());  //根据不同新闻 不同处理
		if(label.length == 3 && (!label[2].equals("")))
			return label[2]+"-"+sourceBuf;
		else
			return sourceBuf;
	}
	@Override
	public String findNewsCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		if(label.length < 1)
			return null;
		else
			return label[0];
	}
	@Override
	public String findNewsOriginalCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		if(label.length < 1)
			return null;
		if(label.length == 2)
			return label[0]+"-" +label[1];
		else
			return label[0];
	}
	//获取新闻评论
	@Override
	public String findNewsComment(String url) {
		// TODO Auto-generated method stub
		//url = "http://news.163.com/14/1114/15/AB18IT2H00014JB6.html#f=wlist";
		String s1 = "http://comment.news.163.com/news3_bbs/";
		String s2 = ".html"; 
		String commentUrl;   //http://comment.news.163.com/news3_bbs/AB0V4LPH00014JB6.html
		commentUrl = s1+url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+s2;
		return commentUrl;
	}
	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}
	

	
	
}

class SINANews implements FindLinks{

	@Override
	public Queue<String> findThemeLinks(String themeLink , String themeLinkReg) {
		// TODO Auto-generated method stub
		return null;
	}

	public Queue<String> findContentLinks(Queue<String> themeLink,String contentLinkReg) {
		// TODO Auto-generated method stub
		return null;
	}

	public String findNewsTitle(String html , String[] label , String buf) {
		// TODO Auto-generated method stub
		return null;
	}

	public String findNewsOriginalTite(String html , String[] label , String buf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsContent(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsImages(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsTime(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsSource(String html , String[] label) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findNewsOriginalSource(String html,String[] label) {
		// TODO Auto-generated method stub
		if(label.length < 1)
			return null;
		else
			return label[0];
	}

	@Override
	public String findNewsCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public String findNewsOriginalCategroy(String html , String[] label) {
		// TODO Auto-generated method stub
		if(label.length < 1)
			return null;
		if(label.length == 2)
			return label[0]+"-" +label[0];
		else
			return label[0];
	}

	@Override
	public String findNewsComment(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String findContentHtml(String url) {
		// TODO Auto-generated method stub
		//url = "http://news.163.com/14/1114/15/AB18IT2H00014JB6.html#f=wlist";
		String s1 = "http://comment.news.163.com/news3_bbs/";
		String s2 = ".html"; 
		String commentUrl;   //http://comment.news.163.com/news3_bbs/AB0V4LPH00014JB6.html
		commentUrl = s1+url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+s2;
		return commentUrl;
	}

	@Override
	public String HandleHtml(String html, String one) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String HandleHtml(String html, String one, String two) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

public class GetNewsFromPortals {
	
	public static void main(String[] args){
		NETEASENews test = new NETEASENews("gb2312");  //网易gb2312
		test.getGuoNeiNews();
	}
	
}


