package com.uestc.spider.www;

import java.util.Vector;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class GetLink {
	public String url;
	public Vector<String> link = new Vector<String>();
	public GetLink(String url){
		this.url = url;
	}
	
	public Vector<String> getLink() throws ParserException{
		
		Parser parser = new Parser(url);
		parser.setEncoding("utf-8");
		NodeList nodeList = parser.extractAllNodesThatMatch(new NodeFilter(){
			public boolean accept(Node node)
            {
              if (node instanceof LinkTag)// ±ê¼Ç
                return true;
              return false;
            }
			
		});
		
		for (int i = 0; i < nodeList.size(); i++)
	      {
	        LinkTag n = (LinkTag) nodeList.elementAt(i);
	        System.out.print(n.getStringText() + " ==>> ");
	        System.out.println(n.extractLink());
	        link.add(n.extractLink());
	      }
		return link;
	}
	
	public static void main(String args[]) throws ParserException{
		
		GetLink test = new GetLink("http://e.chengdu.cn/html/2014-10/08/node_2.htm");
		test.getLink();
	}
	
}
