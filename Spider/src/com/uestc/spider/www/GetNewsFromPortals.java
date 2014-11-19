package com.uestc.spider.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

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
	
	String DBName  ;           			//数据库名称
	String DBTable  ;       			//表名
	private String[] newsTitleLabel;     //新闻标题标签 t
	private String[] newsContentLabel ;  //新闻内容标签 
	private String[] newsTimeLabel ;   //新闻时间
	private String[] newsSourceLabel ; //（3个参数）新闻来源 同新闻时间
	private String[] newsCategroyLabel ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
//	private String[] news
	
	public NETEASENews(String encode, String[] newsTitle ,String[] newsContent ,String[] newsTime,String[] newsSource,String[] newsCategroy ){
		this.ENCODE = encode ;
		this.newsTitleLabel = newsTitle ;
		this.newsContentLabel = newsContent ;
		this.newsTimeLabel = newsTime ;
		this.newsSourceLabel = newsSource ;
		this.newsCategroyLabel = newsCategroy ;
		
		
		
	}
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
	public String newsThemeLinksReg ; //= "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
			
	//新闻内容links的正则表达式
	public String newsContentLinksReg ; //= "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=dlist";
	
	//新闻主题link
	public String theme ;
	
	//评论正则
	public String commentReg ;  //= "http://comment.news.163.com/news3_bbs/";
	
//	public NETEASENews(String theme){
//		
//		this.theme = theme ;
//	}
	//获取国内新闻
	public void getGuoNeiNews(){
		/*初始化各个标签等
		 * 
		 * */
		DBName = "NETEASE";   //数据库名称
		DBTable = "GUONEI";   //表名
		newsTitleLabel = new String[]{"title",""};     //新闻标题标签 title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //新闻内容标签 "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //新闻时间"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","网易新闻-国内新闻"}; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		newsCategroyLabel = new String[]{"国内" , "网易新闻-国内新闻-http://news.163.com/domestic/"} ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
		commentReg = "http://comment.news.163.com/news3_bbs/";
		
		CRUT crut = new CRUT(DBName ,DBTable);
		//国内新闻 首页链接
		theme = "http://news.163.com/domestic/";
		
		//新闻主题links的正则表达式
		newsThemeLinksReg = "http://news.163.com/special/0001124J/guoneinews_[0-9]{1,2}.html#headList";
		
		//新闻内容links的正则表达式 (http://view.163.com/14/1119/10/ABDHAKC500012Q9L.html#f=dlist)
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
			String url = guoNeiNewsContent.poll();
			String html = findContentHtml(url);  //获取新闻的html
			System.out.println(url);
//			System.out.println(html);
			i++;
//			System.out.println(findNewsComment(url));
//			System.out.println("\n");
			crut.add(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsOriginalTitle(html,newsTitleLabel,""),findNewsOriginalTitle(html,newsTitleLabel,""), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url,commentReg), findNewsSource(html,newsSourceLabel),
					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
		}
		System.out.println(i);
		
		

	}
	
	//获取社会新闻
	
	public void getSheHuiNews(){
		/*初始化各个标签等
		 * 
		 * */
		DBName = "NETEASE";   //数据库名称
		DBTable = "SHEHUI";   //表名
		newsTitleLabel = new String[]{"title",""};     //新闻标题标签 title or id=h1title
		newsContentLabel = new String[]{"id" ,"endText"};  //新闻内容标签 "id","endText"
		newsTimeLabel = new String[]{"class","ep-time-soure cDGray"};   //新闻时间"class","ep-time-soure cDGray"  
		newsSourceLabel =new String[]{"class","ep-time-soure cDGray","网易新闻-社会新闻"}; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		newsCategroyLabel = new String[]{"社会" , "网易新闻-社会新闻-http://news.163.com/shehui/"} ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
		commentReg = "http://comment.news.163.com/news_shehui7_bbs/";
		
		CRUT crut = new CRUT(DBName ,DBTable);
		//国内新闻 首页链接
		theme = "http://news.163.com/shehui/";
		
		//新闻主题links的正则表达式
		newsThemeLinksReg = "http://news.163.com/special/00011229/shehuinews_[0-9]{1,2}.html#headList";
		
		//新闻内容links的正则表达式http://focus.news.163.com/  
		newsContentLinksReg = "http://news.163.com/[0-9]{2}/[0-9]{4}/[0-9]{2}/(.*?).html#f=s((list)|(focus))";
		
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
		//保存社会新闻主题links
		Queue<String> sheHuiNewsTheme = new LinkedList<String>();
		sheHuiNewsTheme = findThemeLinks(theme,newsThemeLinksReg);
//		System.out.println(sheHuiNewsTheme);
		
		//获取社会新闻内容links
		Queue<String>sheHuiNewsContent = new LinkedList<String>();
		sheHuiNewsContent = findContentLinks(sheHuiNewsTheme,newsContentLinksReg);
//		System.out.println(sheHuiNewsContent);
		//获取每个新闻网页的html
		int i = 0;
		while(!sheHuiNewsContent.isEmpty()){
			String url = sheHuiNewsContent.poll();
			String html = findContentHtml(url);  //获取新闻的html
			System.out.println(url);
//			System.out.println(findNewsTitle(html,new String[]{"title",""},"_网易新闻中心"));
//			System.out.println(findNewsContent(html,new String[]{"id" ,"endText"}));
//			System.out.println(findNewsTime(html,new String[]{"class","ep-time-soure cDGray"}));
//			System.out.println(findNewsOriginalCategroy(html,new String[]{"社会","网易新闻-社会新闻"}));
			System.out.println(findNewsComment(url,commentReg));
			i++;
			
//			System.out.println(findNewsComment(url));
//			System.out.println("\n");
//			crut.add(findNewsTitle(html,newsTitleLabel,"_网易新闻中心"), findNewsOriginalTitle(html,newsTitleLabel,""),findNewsOriginalTitle(html,newsTitleLabel,""), findNewsTime(html,newsTimeLabel),findNewsContent(html,newsContentLabel),findNewsComment(url), findNewsSource(html,newsSourceLabel),
//					findNewsOriginalSource(html,newsSourceLabel), findNewsCategroy(html,newsCategroyLabel), findNewsOriginalCategroy(html,newsCategroyLabel), url, "");
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
						if(!themelinks.contains(n.extractLink()))
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
	public String findNewsOriginalTitle(String html , String[] label,String buf) {
		// TODO Auto-generated method stub
		String titleBuf ;
		if(label[1].equals("")){
			titleBuf = HandleHtml(html,label[0]);
		}else{
			titleBuf = HandleHtml(html,label[0],label[1]);
		}
//		if(titleBuf.contains(buf))
//			titleBuf = titleBuf.substring(0, titleBuf.indexOf(buf))	;
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
		if(contentBuf == ""){
			contentBuf = HandleHtml(html ,"class","feed-text");
			System.out.println(contentBuf);
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
		String timeBuf ="";
		if(label[1].equals("")){
			timeBuf = HandleHtml(html , label[0]);
		}else{
			timeBuf = HandleHtml(html , label[0],label[1]);
		}
		if(timeBuf == ""){
			timeBuf = HandleHtml(html,"id","ptime");
//			return timeBuf;
		}else
			timeBuf = timeBuf.substring(9, 28);  //根据不同新闻 不同处理
		return timeBuf;
	}
	@Override
	public String findNewsSource(String html ,String[] label) {
		// TODO Auto-generated method stub
		if(label.length == 3 && (!label[2].equals("")))
			return label[2];
		else
			return null;
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
	public String findNewsComment(String url ,String reg) {
		String result = "";
		// TODO Auto-generated method stub
		//http://comment.news.163.com/news_shehui7_bbs/ABBLUKEC00011229.html
		//url = "http://news.163.com/14/1114/15/AB18IT2H00014JB6.html#f=wlist";
		String[] s1 = {"http://comment.news.163.com/news_shehui7_bbs/","http://comment.news.163.com/news_guonei8_bbs/","http://comment.news.163.com/news3_bbs/","http://comment.news.163.com/news1_bbs/","http://comment.news.163.com/news2_bbs/","http://comment.news.163.com/news4_bbs/","http://comment.news.163.com/news5_bbs/","http://comment.news.163.com/news6_bbs/","http://comment.news.163.com/news7_bbs/","http://comment.news.163.com/news8_bbs/","http://comment.news.163.com/news9_bbs/",
				"http://comment.news.163.com/news_guonei1_bbs/","http://comment.news.163.com/news_guonei2_bbs/","http://comment.news.163.com/news_guonei3_bbs/","http://comment.news.163.com/news_guonei4_bbs/","http://comment.news.163.com/news_guonei5_bbs/","http://comment.news.163.com/news_guonei6_bbs/","http://comment.news.163.com/news_guonei7_bbs/","http://comment.news.163.com/news_guonei9_bbs/",
				"http://comment.news.163.com/news_shehui1_bbs/","http://comment.news.163.com/news_shehui2_bbs/","http://comment.news.163.com/news_shehui3_bbs/","http://comment.news.163.com/news_shehui4_bbs/","http://comment.news.163.com/news_shehui5_bbs/","http://comment.news.163.com/news_shehui6_bbs/","http://comment.news.163.com/news_shehui8_bbs/","http://comment.news.163.com/news_shehui9_bbs/"};
		String s2 = ".html"; 
		String commentUrl;   //http://comment.news.163.com/news3_bbs/AB0V4LPH00014JB6.html
		String s3 = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."))+s2;
		commentUrl = reg+s3;
		
		URL link = null;
		int i = 0;
		while(true){
			HttpURLConnection  httpUrlConnection;
			int state = 0;
			try{
				httpUrlConnection = (HttpURLConnection) new URL(commentUrl).openConnection(); //创建连接
				state = httpUrlConnection.getResponseCode();
				httpUrlConnection.disconnect();
			}catch (MalformedURLException e) {
//	          e.printStackTrace();
//				System.out.println("该连接"+url+"网络有故障，已经无法正常链接，无法获取新闻");
//				return null ;
			} catch (IOException e) {
	          // TODO Auto-generated catch block
//	          e.printStackTrace();
//				System.out.println("该连接"+url+"网络超级慢，已经无法正常链接，无法获取新闻");
//				return null ;
			}
			if(state == 200 || state == 201){
				System.out.println("we win");
					try {
						link = new URL(commentUrl);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
					break;
			}else{
				System.out.println("we lost");
				commentUrl = s1[i]+s3;
			}
			
			if(i == s1.length)
				break;
			i++ ;
		}
        WebClient wc=new WebClient();
        WebRequest request=new WebRequest(link); 
        request.setCharset(ENCODE);
//        其他报文头字段可以根据需要添加
        wc.getCookieManager().setCookiesEnabled(true);//开启cookie管理
        wc.getOptions().setJavaScriptEnabled(true);//开启js解析。对于变态网页，这个是必须的
        wc.getOptions().setCssEnabled(true);//开启css解析。对于变态网页，这个是必须的。
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        wc.getOptions().setThrowExceptionOnScriptError(false);
        wc.getOptions().setTimeout(10000);
        //准备工作已经做好了
        HtmlPage page= null;
        try {
			page = wc.getPage(request);
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
        if(page==null)
        {
            System.out.println("采集 "+commentUrl+" 失败!!!");
            return null;
        }
        String content=page.asText();//网页内容保存在content里
        if(content==null)
        {
            System.out.println("采集 "+commentUrl+" 失败!!!");
            return null;
        }else;
        	//System.out.println(content);
        if(!content.contains("去跟贴广场看看"))
        	return null;
        String ss = content.substring(content.indexOf("去跟贴广场看看")+7, content.indexOf("跟贴用户自律公约"));
//        System.out.println(ss);
        result = ss;
        ss = null; 
        content = content.substring(0, content.indexOf("文明社会，从理性发贴开始。谢绝地域攻击。"));
        content = content.replaceAll("\\s+", "");
        String commentss = "发表(.*?)顶";
//        String source = "发表哈哈哈啊哈顶顶顶顶发表家具啊姐姐顶发表哈哈哈顶发表。。。。顶发表；；；；顶发表【【】。；；；顶发表。。、；匹配顶发表(.*?)顶";
        Pattern newPage = Pattern.compile(commentss);
        
        Matcher themeMatcher = newPage.matcher(content);
        while(themeMatcher.find()){
        	String mm = themeMatcher.group();
        	mm = mm.replaceAll("发表", "");
        	mm = mm.replaceAll("顶", "");
//        	System.out.println(mm);
        	result = result +"  ;"+ mm +"  ;";
        	mm = null;
        }
		commentss = null ;
		return result;
	}
	@Override
	public void handle(String DBName ,String DBTable,String html ,String url) {
		// TODO Auto-generated method stub
//		CRUT crut = new CRUT(DBName ,DBTable);
//		crut.add(title, originalTitle, titleContent, time, content, comment, newSource, originalSource, category, originalCategroy, url, image);
		
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

	public String findNewsOriginalTitle(String html , String[] label , String buf) {
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
	public String findNewsComment(String url,String reg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handle(String DBName,String DBTable,String html ,String url) {
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
		/*private String[] newsTitleLabel;     //新闻标题标签 title or id=h1title
		private String[] newsContentLabel ;  //新闻内容标签 "id","endText"
		private String[] newsTimeLabel ;   //新闻时间"class","ep-time-soure cDGray"
		private String[] newsSourceLabel ; //（3个参数）新闻来源 同新闻时间"class","ep-time-soure cDGray" 再加上一个"网易新闻-国内新闻"
		private String[] newsCategroyLabel ; // "国内" "网易新闻-国内新闻-http://news.163.com/domestic/"
		 * 
		 * */
		NETEASENews test = new NETEASENews("gb2312");  //网易gb2312
		test.getSheHuiNews();
	}
	
}


