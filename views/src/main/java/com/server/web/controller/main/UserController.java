package com.server.web.controller.main;

import com.google.gson.*;
import com.server.common.annotation.Log;
import com.server.common.enums.BusinessType;
import com.server.shiro.realm.MyRealm;
import com.server.system.pojo.ResponseBean;
import com.server.system.pojo.User;
import com.server.system.service.UserService;
import com.server.web.controller.BaseController;
import com.server.web.controller.BasicController;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.*;

/** 
* @ClassName: UserController 
* @Description: TODO(用户管理) 
* @author 冯庆国 
* @date 2017年3月21日 上午9:33:09 
*  
*/
@Controller
@RequestMapping("user")
public class UserController extends BasicController {
	@Autowired
	private UserService service;
	@Autowired
	private MyRealm dbRealm;

	/**
	 * <p>Title: Contrall基类构造函数</p>
	 * <p>Description: 初始化一些Contrall的公有属性、方法、事件等</p>
	 */
	public UserController() {
		super(UserController.class);
	}

	/**
	 * @Title: actorInfo
	 * @Description: TODO(弹窗)
	 * @param @param id
	 * @param @return 设定文件
	 * @return ModelAndView 返回类型
	 * @throws
	 */
	@RequestMapping(value = "userInfo", method = RequestMethod.GET)
	@ResponseBody
	public String userInfo(String id) {
		User user=null;
		try {
			user = service.selectByPrimaryKey(id);
		} catch (Exception e) {
			logger.error("用户ID查询报错", e);
		}
		gson=new Gson();
		return gson.toJson(user);
	}

	/**
	 * @Title: bind
	 * @Description: TODO(数据绑定)
	 * @param @param page
	 * @param @param rows
	 * @param @param starttime
	 * @param @param endtime
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping(value = "bind", method = RequestMethod.POST)
	@ResponseBody
	public String bind(int page, int rows, String starttime, String endtime) {
		String strJson = "";
		page = (page == 0 ? 1 : page);
		rows = (rows == 0 ? 15 : rows);
		int start = (page - 1) * rows;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("p1", start);
		map.put("p2", rows);
		if (starttime == null || starttime.trim().equals("")) {
			map.put("starttime", null);
		} else {
			try {
				map.put("starttime", datetimeFormat.parseObject(starttime));
			} catch (ParseException e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
		if (endtime == null || endtime.trim().equals("")) {
			map.put("endtime", null);
		} else {
			try {
				map.put("endtime", datetimeFormat.parseObject(endtime));
			} catch (ParseException e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
		List<User> list = null;
		int count = 0;
		try {
			list = service.findByPage(map);
			count = service.pageCount(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("total", count);
		jsonMap.put("rows", list);
		gson = new GsonBuilder().registerTypeAdapter(User.class,
				new JsonSerializer<User>() {
					@Override
					public JsonElement serialize(User arg0, Type arg1,JsonSerializationContext arg2) {
						JsonObject json = new JsonObject();
						json.addProperty("id", arg0.getId());
						json.addProperty("name", arg0.getName());
						json.addProperty("username", arg0.getUsername());
						json.addProperty("mobile", arg0.getMobile());
						json.addProperty("email", arg0.getEmail());
						json.addProperty("memo", arg0.getMemo());
						json.addProperty("createtime",
								datetimeFormat.format(arg0.getCreatetime()));
						json.addProperty("edittime",
								datetimeFormat.format(arg0.getEdittime()));
						return json;
					}
				}).create();
		strJson = gson.toJson(jsonMap);
		return strJson;
	}
	/**
	 * @Title: add
	 * @Description: TODO(新增)
	 * @param @param jsonStr
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@Log(title = "用户管理", businessType = BusinessType.INSERT)
	@RequestMapping(value = "add", method = RequestMethod.POST)
	@ResponseBody
	public String add(String jsonStr) {
		gson = new Gson();
		User user = gson.fromJson(jsonStr, User.class);
		String result = "";
		try {
			user.setId(UUID.randomUUID().toString());
			user.setPassword("12345");
			user.setCreatetime(new Date());
			user.setEdittime(new Date());
			user.setIsdel(1);
			service.insertSelective(user);
			result = "{'result':true}";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			result = "{'result':false,'msg':'保存失败'}";
		}
		return result;
	}

	/**
	 * @Title: edit
	 * @Description: TODO(编辑)
	 * @param @param jsonStr
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@Log(title = "用户管理", businessType = BusinessType.UPDATE)
	@RequestMapping(value = "edit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean edit(String jsonStr) {
		ResponseBean responseBean = null;
		gson = new Gson();
		User user = gson.fromJson(jsonStr, User.class);
		try {
			user.setEdittime(new Date());
			service.updateByPrimaryKeySelective(user);
			responseBean=new ResponseBean(HttpStatus.OK.value(),"保存成功",true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			responseBean=new ResponseBean(HttpStatus.OK.value(),"保存失败",true);
		}
		return responseBean;
	}

	/**
	 * @Title: reload
	 * @Description: TODO(密码重置)
	 * @param @param id
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@Log(title = "用户管理", businessType = BusinessType.UPDATE)
	@RequestMapping(value = "reload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean reload(String id) {
		ResponseBean responseBean=null;
		try {
			User user = new User();
			user=service.selectByPrimaryKey(id);
			user.setPassword("123456");
			SecurityUtils.getSubject().logout();
			service.updateByPrimaryKeySelective(user);
			responseBean=new ResponseBean(HttpStatus.OK.value(),"密码重置成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			responseBean=new ResponseBean(HttpStatus.OK.value(),"密码重置失败",true);
		}
		return responseBean;
	}
	/** 
	* @Title: delete
	* @Description: TODO(删除) 
	* @param @param id
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	@Log(title = "用户管理", businessType = BusinessType.DELETE)
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean delete(String id) {
		ResponseBean responseBean=null;
		try {
			User user = new User();
			user.setId(id);
			user.setEdittime(new Date());
			user.setIsdel(0);
			service.updateByPrimaryKeySelective(user);
			responseBean=new ResponseBean(HttpStatus.OK.value(),"删除成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			responseBean=new ResponseBean(HttpStatus.OK.value(),"删除失败",true);
		}
		return responseBean;
	}
	
	/** 
	* @Title: validataUsername 
	* @Description: TODO(验证用户登录名是否唯一) 
	* @param @param id
	* @param @param name
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	@RequestMapping(value = "validataUsername", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean validataUsername(String id,String name){
		ResponseBean responseBean=null;
		try {
			User user =service.get(name);
			if(id.equals("0")&&user==null||!id.equals("0")&&user==null){
				responseBean=new ResponseBean(HttpStatus.OK.value(),"用户名可用",true);
			}
			if(!id.equals("0")&&user!=null&&user.getId().equals(id)){
				responseBean=new ResponseBean(HttpStatus.OK.value(),"用户名可用",true);
			}
		} catch (Exception e) {
			logger.error("用户登录名验证", e);
			e.printStackTrace();
			responseBean=new ResponseBean(HttpStatus.OK.value(),"用户名不可用",false);
		}
		return responseBean;
	}
}
