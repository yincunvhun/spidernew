package com.uestc.spider.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

/*
 * 针对成都商报的新闻爬虫
 * 获取成都商报一天的所有新闻
 * 新闻包括 新闻题目，发布时间，新闻内容，报道记者以及其PDF格式的新闻文件
 * 可配置！！
 * */
public class CDSB implements Runnable {

	HttpURLConnection httpUrlConnection;
    InputStream inputStream;
    BufferedReader bufferedReader;
    String url;              		//要处理的url
    String text = "";       		 //存储url的html内容
    public String title;  			//新闻标题
    public String content;			//新闻内容
    public String time;             //新闻发布时间
    public String officeName;       //报社名称
  
    public CDSB(String url) {
  
        try {
        	this.url = url;
//        	this.baseUrl = ;
        
        } catch (Exception e) {
        	
        	e.printStackTrace();
        }
  
        try {
            httpUrlConnection = (HttpURLConnection) new URL(url).openConnection(); //创建连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
  
//        System.out.println("---------start-----------");
  
        Thread thread = new Thread(this);
        thread.start();
        try {thread.join();} catch (InterruptedException e) {e.printStackTrace();}
  
//        System.out.println("----------end------------");
    }
  
    public void run() {
        // TODO Auto-generated method stub
        try {
            httpUrlConnection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
  
        try {
            httpUrlConnection.setUseCaches(true); //使用缓存
            httpUrlConnection.connect();           //建立连接
        } catch (IOException e) {
            e.printStackTrace();
        }
  
        try {
            inputStream = httpUrlConnection.getInputStream(); //读取输入流
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); 
            String string;
            StringBuffer sb = new StringBuffer();
            while ((string = bufferedReader.readLine()) != null) {
            	sb.append(string);
            	sb.append("\n");
            }
            text = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
                httpUrlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
  
        }
  
    }

 
   /*
     	只需要一个参数就可以判断的标签，比如title 
    * */
   String handle(String html ,String one){
	   NodeFilter filter = new HasAttributeFilter(one);
	   String buf = "";
	   try{
		    Parser parser = Parser.createParser(html, "GB2312");
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
   
  /*
   * 需要两个参数才能判断准确的内容：比如 content-title ，ozoom等
   */
   String handle(String html ,String one ,String two){
	   NodeFilter filter = new HasAttributeFilter(one,two);
	   String buf = "";
	   try{
		    Parser parser = Parser.createParser(html, "GB2312");
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
 /*
  * 新闻标题
  * */ 
 String handleTitle(String html){
	   title = handle(html,"title");
	   title += handle(html,"class","content-title");
//	   System.out.println(title);
	   return title;
   }
/*
 * 新闻内容
 * */
   String handleContent(String html){
	   
	   content = handle(html,"id","ozoom");
//	   content = content.replaceAll("\\n", "");
	   System.out.println(content);
	   return content;
   }
 /*
  * 新闻图片 图片名为：时间+后缀（比如：20140910.jpg）
  * */
   void handleImage(String html){
	   GetImage image = new GetImage();
	   image.fileName = handleTime(html)+".jpg";
	   image.getImage(html);
	   
   }
   
  /*
  * 新闻pdf
  * */
   void handlePDF(String url){
	   
	   new GetPdf(url);
   }
   /*
    * 新闻发布时间
    * */
   String handleTime(String html){
	
	   time = handle(html,"class","header-today");
//	   time = time.substring(0,12);  //只获取时间
	   if(time.contains(" "))
		   time = time.replaceAll("\\s", "");
	   System.out.println(time);
	   return time;
   }
  /*
   * 获取报社名称
   * 
   * */
   String handleOfficeName(String html){
	   officeName = handle(html,"class","info");
	   System.out.println(officeName);
	   return officeName;
   }
   /*
    * 新闻相关内容的存储
    * 标题 时间  内容 报社名称
    * */
   public static void memory(String url){
	   
	   CRUT crut = new CRUT();
	   CDSB cdsb = new CDSB(url);
	   crut.add(cdsb.handleTitle(cdsb.text),cdsb.handleTime(cdsb.text),
			   cdsb.handleContent(cdsb.text),cdsb.handleOfficeName(cdsb.text));
   }
   
   
    public static void main(String[] args) throws Exception {
    	
    	String url1 = "http://e.chengdu.cn/html/2014-09/10/content_487767.htm";
    	String url2 = "http://paper.people.com.cn/rmrb/html/2014-09/05/nw.D110000renmrb_20140905_1-01.htm";
    	String url3 =  "http://www.csdn.net";
    	memory(url1);
//    	CDSB test = new CDSB(url1);	
//    	test.handleOfficeName(test.text);
//    	CRUT crut = new CRUT();
//    	crut.add("2014",test.handleTitle(test.text),test.handleContent(test.text),"cdsb");

    }
}
