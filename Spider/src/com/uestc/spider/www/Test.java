package com.uestc.spider.www;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.QueryOperators;
import com.mongodb.util.JSON;

/*
 * 1.先把华西都市报趴下来
 * 2.利用多线程爬取
 * 3.利用线程获取当天的新闻
 * 4.找到内存使用增加的原因！！
 * 
 * 2014.10.27
 * 1.实现多线程爬取：一个控制线程，多个爬取线程（每个线程负责一个报社新闻）
 * 
 * 2.尽可能的模板化
 * */
public class Test {
	
	
	 
	public static void main(String args[]) throws UnknownHostException{

	}
}
