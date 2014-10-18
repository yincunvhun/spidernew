package com.uestc.spider.www;

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

public class GetLink {
	
//	public String url;
	//上一期
	public Queue<String> linkLast = new LinkedList<String>();
	//下一期
	public Queue<String> linkNext = new LinkedList<String>();
	//保存主题链接
	public Queue<String> linkTheme = new LinkedList<String>();
	//pdf
	public Queue<String> linkPdf = new LinkedList<String>();
	//保存每个主题内容的链接
	public Queue<String> linkContent = new LinkedList<String>();
	//保存已经访问过的链接
	public Queue<String> linkVisit = new LinkedList<String>();
	
//	public GetLink(String url){
//		this.url = url;
//	}
	//该url参数必须是某一板块的themeurl 不能使某一个具体新闻的themeurl
	public void getLink(String themeUrl) throws ParserException{
		
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
		Pattern newContent = Pattern.compile("http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm");
		//PDF正则表达式
		Pattern newPdf = Pattern.compile("http://e.chengdu.cn/page/[0-9]{1}/[0-9]{4}-[0-9]{2}/[0-9]{2}/[0-9]{2}/[0-9]{10}_pdf.pdf");
		
		for (int i = 0; i < nodeList.size(); i++)
	      {
	        LinkTag n = (LinkTag) nodeList.elementAt(i);
//	        System.out.print(n.getStringText() + "==>> ");
//	        System.out.println(n.extractLink());
	        //某一版
	        Matcher themeMatcher = newPage.matcher(n.extractLink());
	        //具体的内容
	        Matcher contentMatcher = newContent.matcher(n.extractLink());
	        //PDF
	        Matcher pdfMatcher = newPdf.matcher(n.extractLink());
	        
	        if(!linkVisit.contains(n.extractLink())){
	        	
//	        	if(n.getStringText().equals("上一期")){
//	        		System.out.println("zhe li zen me le ");
//	        		linkLast.offer(n.extractLink());
////	        		linkVisit.offer(n.extractLink());
//	        	}else if(n.getStringText().equals("下一期")){
//	        		linkNext.offer(n.extractLink());
////	        		linkVisit.offer(n.extractLink());
//	        	}else{
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
	      }
	}
	
	
	public void allWeWillDo(String themeUrl) throws Exception{
		
		
		linkTheme.offer(themeUrl);
//			linkVisit.offer(n.extractLink());
			while(!linkTheme.isEmpty()){
				getLink(linkTheme.poll());
				while(!linkContent.isEmpty()){
					StringBuffer s = new StringBuffer(linkContent.poll());
					CDSB cdsb = new CDSB(s.toString());
					cdsb.memory(s.toString());
				}
				System.out.println("我正在把获取的新闻存入数据库...");
			}
			
		
		
//		while(!linkNext.isEmpty()){
//			
//			getLink(linkNext.poll());
//			
//			while(!linkTheme.isEmpty()){
//				
//				getLink(linkTheme.poll());
//				
//				while(!linkContent.isEmpty()){
//					
//					
//					StringBuffer s = new StringBuffer(linkContent.poll());
//					
//					CDSB cdsb = new CDSB(s.toString());
//					
//					cdsb.memory(s.toString());
//					
//				}
//			}
//			
//		}
	
	}
	public void result(){
		String url = "http://e.chengdu.cn/html/2014-10/08/node_2.htm";
//		for(int i = 2014 ; i > 20)
	}
	public static void main(String args[]) throws Exception{
		long start = System.currentTimeMillis();    
		GetLink test = new GetLink();
		String url = "http://e.chengdu.cn/html/2014-10/16/node_2.htm";
		System.out.println(" 我正在努力搜索新闻...");
		test.allWeWillDo(url);
		System.out.println("程序执行完毕...");
		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
	
}
