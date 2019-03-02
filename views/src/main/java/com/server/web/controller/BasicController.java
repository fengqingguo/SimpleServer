package com.server.web.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/** 
* @ClassName: BasicController 
* @Description: TODO(Contraller基类) 
* @author lyf 
* @date 2015年8月7日 下午1:55:48 
*  
*/

public class BasicController {
	
	/** 
	* @Fields dateFormat : TODO(将日期格式化：yyyy-MM-dd) 
	*/ 
	protected SimpleDateFormat dateFormat;
	
	protected Gson gson;
	/** 
	* @Fields datetimeFormat : TODO(将日期格式化：yyyy-MM-dd HH:mm:ss) 
	*/ 
	protected SimpleDateFormat datetimeFormat;
	
	/** 
	* @Fields logger : TODO(日志对象) 
	*/ 
	protected Logger logger;
	private Class<?> classz;
	
	/** 
	* <p>Title: Contrall基类构造函数</p> 
	* <p>Description: 初始化一些Contrall的公有属性、方法、事件等</p>  
	*/
	public BasicController(Class<?> classz){
		this.classz=classz;
		init();
	}
	
	private void init(){
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger = LoggerFactory.getLogger(classz);
	}
	
}
