package com.uestc.spider.www;

import java.util.Queue;
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
		ThreadPoolExecutor cg = new ThreadPoolExecutor(10,50,30,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueDeep),  
                new ThreadPoolExecutor.DiscardOldestPolicy());
		/*
		 * 手动一个个的加入报社配置，比较繁琐
		 * */
	/*	for(int i = 0 ; i < 50 ; i++){
			
			try{
				Thread.sleep(2);
			}catch(InterruptedException e){
				System.out.println("我失眠了。");
			}
			while(getQueueSize(cg.getQueue()) >= queueDeep){
				
				System.out.println("队列已满，等待3秒再添加任务");
				try{
					Thread.sleep(3000);
				}catch(InterruptedException e){
					System.out.println("我失眠了。");
				}
			}
			
		}
		*/
		//北京青年报 新闻标题：h1  内容：class="contnt" 日期：class="fst" 新闻来源：北京青年报  新闻分类：id=PageLink，待处理字符串为""
		//图片配置src="../../../images/2013-10/30/B02/gly3a1262_b.jpg"，image正则表达："IMG src=\"(.*?)iamges(.*?)_b.jpg\"" 路径表达式："http:\"?(.*?)(\"|>|\\s+)"辅助字符串"http://epaper.ynet.com"
		//主题链接：http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm 内容链接：http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,6}.htm?div=-1
		//s1 = http://epaper.ynet.com/html/ s2 = - s3 = / s4 = /node_1331.htm
		TaskThreadPool ynet = new TaskThreadPool("ynet","cg",new String[]{"h1",""},new String[]{"class","contnt"},new String[]{"class","fst"},new String[]{"北京青年报","北京青年报新闻热线：65902020; 广告热线：400-188-8610;小红帽”发行热线：6775-6666;北青网新闻热线：65901660"},new String[]{"id","PageLink"},"",
				"http://epaper.ynet.com/","IMG src=\"(.*?)iamges(.*?)_b.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
				"http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm","http://epaper.ynet.com/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,6}.htm?div=-1","http://epaper.ynet.com/","-","/","/node_1331.htm");        //0,"",new String[]{"",""}
		
		cg.execute(ynet); //青年报加入线程池
		
		//北京晚报：新闻标题：title 内容(这个不对？？？为什么？？)：id=ozoom 日期：width="316" 来源 北京晚报 新闻分类：width="145" 待处理字符：""
		//图片配置：(http://bjwb.bjd.com.cn/)!images/2014-11/03/10/wjh4b24_b.jpg image正则表达式："IMG src=\"(.*?)iamges(.*?)_b.jpg\""路径表达式："http:\"?(.*?)(\"|>|\\s+)" 辅助字符串"../../../"
		//主题链接：http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm 内容链接：http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm
		//s1 = http://bjwb.bjd.com.cn/ s2  = - s3 = / s4 = /node_82.htm
		TaskThreadPool bjwb = new TaskThreadPool("bjwb","cg",new String[]{"title",""},new String[]{"id","ozoom"},new String[]{"width","316"},new String[]{"北京晚报","京报集团所属报刊；本网站所有内容属北京日报报业集团所有，未经许可不得转载"},new String[]{"width","145"},"",
				"http://bjwb.bjd.com.cn/","IMG src=\"(.*?)iamges(.*?)_b.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
				"http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{4,5}.htm","http://bjwb.bjd.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{5,7}.htm","http://bjwb.bjd.com.cn/","-","/","/node_82.htm");
		cg.execute(bjwb);
		
		//成都商报：新闻标题：title ，内容 id = ozoom ,日期：class="header-today" 来源：成都商报 新闻分类："width","57%";待处理字符: " - 成都商报|成都商报电子版|成都商报官方网站"
		//图片配置：http://e.chengdu.cn/html/ 正则表达式："img src=\"(.*?)res(.*?)attpic_brief.jpg\"" 路径表达式："http:\"?(.*?)(\"|>|\\s+)" 辅助字符串："../../../"
		//主题链接"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm" 内容链接："http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm"
		//s1 = http://e.chengdu.cn/html/ s2 - s3 / s4 /node_2.htm
		TaskThreadPool cdsb = new TaskThreadPool("cdsb","cg",new String[]{"title",""},new String[]{"id","ozoom"},new String[]{"class","header-today"},new String[]{"成都商报","成都商报网站: http://e.chengdu.cn | 成都商报刊号: CN51-0073成都商报新闻热线: 86612222  "},new String[]{"width","57%"},"- 成都商报|成都商报电子版|成都商报官方网站",
				"http://e.chengdu.cn/html/","img src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
				"http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{1,2}.htm","http://e.chengdu.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{1,6}.htm","http://e.chengdu.cn/html/","-","/","/node_2.htm");
		cg.execute(cdsb);
		
		//成都晚报:新闻标题class="bt1" 内容："class","M_m_cont",日期："style","display:none" 来源：成都晚报 新闻分类(单一新闻看不出具体分类)：class="info" ，待处理字符；"" 
		//图片配置：http://www.cdwb.com.cn/html/ 正则表达式："IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"" 路径："http:\"?(.*?)(\"|>|\\s+)" 辅助："../../../"
		//主题链接：http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{2,4}.htm ；内容http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm
		//s1 = http://www.cdwb.com.cn/html/ s2 - s3 / s4 /node_282.htm
		TaskThreadPool cdwb = new TaskThreadPool("cdwb","cg",new String[]{"class","bt1"},new String[]{"class","M_m_cont"},new String[]{"style","display:none"},new String[]{"成都晚报","新闻热线：962111  \\n 官方微博：http://weibo.com/cdwbwb \\n 订报热线：028-86741226 广告热线：028-86746906 \\n 报社地址：成都红星路二段159号(邮编:610017);成都电子版版权所有：成都晚报社 广告经营许可证号：5101034000060"},new String[]{"class","info"},"",
				"http://www.cdwb.com.cn/html/","IMG src=\"(.*?)res(.*?)attpic_brief.jpg\"","http:\"?(.*?)(\"|>|\\s+)","../../../",
				"http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/node_[0-9]{2,4}.htm","http://www.cdwb.com.cn/html/[0-9]{4}-[0-9]{2}/[0-9]{2}/content_[0-9]{6,8}.htm","http://www.cdwb.com.cn/html/","-","/","/node_282.htm");
		cg.execute(cdwb);
		
		
	
	
	
	}
	private synchronized int getQueueSize(Queue queue){
		
		return queue.size();
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
//	public int flag ;           //第几个线程
	
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
		
//		this.flag = flag ;
		
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
