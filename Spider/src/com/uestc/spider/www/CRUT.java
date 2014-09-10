package com.uestc.spider.www;

import java.io.File;
import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class CRUT {

	static private Mongo mg  = null;
	static private DB db ;
	static private DBCollection users;
	
	public CRUT(){
		
		try {
			
            mg = new Mongo();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
		//获取cdsb DB；如果默认没有创建，mongodb会自动创建
		db = mg.getDB("cdsb");
		//获取users DBCollection；如果默认没有创建，mongodb会自动创建
		users = db.getCollection("cg");
//		System.out.println("我被执行啦");
	}
	//删除数据库
	public void destory() {
        if (mg != null)
            mg.close();
        mg = null;
        db = null;
        users = null;
        System.gc();
    }
	
	/*
	 * 添加数据,新闻内容,其中s的长度必须为偶数
	 * 内容为：新闻题目，新闻内容，报社名称，发布时间
	 * k-v:(name ,time) (content , office)
	 */
	public void add(String newName,String newTime ,String newContent,String newOffice){
		DBObject user = new BasicDBObject();
		user.put(newName, newTime);
		user.put(newContent, newOffice);
		users.insert(user);
//		System.out.println("111");
//		System.out.println(users.find(new BasicDBObject(newName, newTime)).toArray());
		
	}
	
	//添加图片以及PDF文件
	public void add(File file){
		
		
	}
	//查询可以查新闻题目，新闻内容，新闻发布时间，报社名称等
	public void query(String key,String value){
	
		System.out.println(users.find(new BasicDBObject(key, value)).toArray());
		
	}
	
	//查看数据库中所有数据
    private void queryAll() {
    	DBCollection users = db.getCollection("users");
		System.out.println("查询users的所有数据：");
		//db游标
		DBCursor cur = users.find();
		while (cur.hasNext()) {
			 System.out.println(cur.next());
			 
		}
		
    }
  //删除数据
  	 public void remove(String key ,String value) {
  		    users.remove(new BasicDBObject(key, new BasicDBObject("$gte", value))).getN();
  		
  	 }
  	 
    public static void main(String args[]){
    	
    	CRUT test = new CRUT();
		for(String name:mg.getDatabaseNames())
			System.out.println(name);
		String url1 = "http://e.chengdu.cn/html/2014-09/10/content_487767.htm";
		CDSB test1 = new CDSB(url1);	
//		test.queryAll();
//    	test.destory();
//    	test.add("xixi", "2014.9.10", "教师节快乐", "uestc");
//    	test.remove("xi","2014.9.10");
//    	test.add("xi","2014.9.10","jiaoshijiekuailfe","uestc");
    	test.query("2014",test1.handleTitle(test1.text));
    	test.query(test1.handleTitle(test1.text),"2014");
//    	test.query("xixi","2014.9.10");
    }
}
