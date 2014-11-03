package com.uestc.spider.www;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolHandleNews{

	private static int queueDeep = 20 ;
	
	public void createThreadPool(){
		/*   
         * 创建线程池，最小线程数为10，最大线程数为50，线程池维护线程的空闲时间为30秒，   
         * 使用队列深度为20的有界队列，如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，   
         * 然后重试执行程序（如果再次失败，则重复此过程），里面已经根据队列深度对任务加载进行了控制。   
         */ 
		ThreadPoolExecutor cg = new ThreadPoolExecutor(10,100,30,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueDeep),  
                new ThreadPoolExecutor.DiscardOldestPolicy());
	}
	
	public static void main(String[] args){
		
		TaskThreadPool testPool = new TaskThreadPool();
		Thread t = new Thread(testPool);
		t.start();
//		testPool.run();
	}
	
	
}

class TaskThreadPool implements Runnable{

	/**
	 * 很多需要初始化的东西，
	 * 好好想想
	 * 1.数据库 名称 表名
	 * 2.新闻：新闻标题标签Title[] 内容标签Content[] 日期标签date[]  新闻来源 newSource[] 新闻分类 categroy[] 新闻待处理字符串 bufString
	 * 3.图片：图片配置url ，image正则表达式：IMURL_REG 路径表达式：IMSRC_REG 链接辅助字符串 ：imageBuf
	 * 4.新闻主题链接：ThemeLink ,内容链接：contentLink 以及配置字符串
	 * 
	 * */
	public String DBName ;      //数据库名称
	public String DBTable ;     //数据库表名
	
	public String title[];      // 新闻标题
	public String content[];    //新闻内容
	public String date[] ;      //新闻日期
	public String newSource[] ; //新闻来源    //可固定
	public String categroy[] ;  //新闻分类
	public String bufString ;   //新闻待处理字符串
	
	public String imageUrl ;        //图片配置url
	public String imurl_reg ;      //image正则表达式
	public String imscr_reg ;      //图片路径
	public String imageBuf ;       //图片链接辅助字符串
	
	public String themeLink ;       //主题链接正则表达式
	public String contentLink;     //内容链接正则表达式
	public String newurl1 ;        //链接填充内容 1,2,3,4
	public String newurl2 ;
	public String newurl3 ;
	public String newurl4 ;
	
	
	public TaskThreadPool(){
		
	}
	
	//感觉这个构造函数好累啊
	
	public TaskThreadPool(String DBName ,String DBTable ,
			String title[],String content[],String date[],String newSource[] ,String categroy[] ,String bufString,
			String imageurl ,String imurl_reg ,String imsrc_reg,String imageBuf,
			String themeLink ,String contentLink,String newurl1,String newurl2,String newurl3 ,String newurl4){
		
		this.DBName = DBName ;
		this.DBTable = DBTable ;
		
		this.title = title ;
		this.content = content ;
		this.date = date ;
		this.newSource = newSource ;
		this.categroy = categroy ;
		this.bufString = bufString ;
		
		this.imageUrl = imageurl ;
		this.imurl_reg = imurl_reg ;
		this.imscr_reg = imsrc_reg ;
		this.imageBuf = imageBuf ;
		
		this.themeLink = themeLink ;
		this.contentLink = contentLink ;
		this.newurl1 = newurl1 ;
		this.newurl2 = newurl2 ;
		this.newurl3 = newurl3 ;
		this.newurl4 = newurl4 ;
		
		
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread()+"xixi");
	}
	
	
}
