/**   
 * 特别声明：本技术材料受《中华人民共和国着作权法》、《计算机软件保护条例》
 * 等法律、法规、行政规章以及有关国际条约的保护，武汉中地数码科技有限公
 * 司享有知识产权、保留一切权利并视其为技术秘密。未经本公司书面许可，任何人
 * 不得擅自（包括但不限于：以非法的方式复制、传播、展示、镜像、上载、下载）使
 * 用，不得向第三方泄露、透露、披露。否则，本公司将依法追究侵权者的法律责任。
 * 特此声明！
 * 
   Copyright (c) 2013,武汉中地数码科技有限公司
 */
package com.zondy.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.config.XmlConfig;
import com.zondy.hibernate.bean.FhyVo;
import com.zondy.hibernate.bean.PersonVo;
import com.zondy.hibernate.dao.HibernateDAOImpl;
import com.zondy.listener.ApplicationListener;
import com.zondy.service.impl.FhyScoreImpl;
import com.zondy.util.DateUtils;
import com.zondy.util.ExcelUtils;
import com.zondy.util.HttpRequest;
import com.zondy.util.PinyinUtils;
import com.zondy.util.RandomUtils;
import com.zondy.util.SystemUtils;

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class ApiServlet extends HttpServlet {
	
	private static Logger log = Logger.getLogger(ApiServlet.class);
	
	public enum AjaxMethod{
		loaddata,
		saveOrUpdate,
		//缓存数据字典文件
		cachedic,
		getfhy,
		importfhy,
		queryfhy,
		uploadHeadimg,
		visitlog,
		useroplog,
		savelog,
		login,
		pagedata,
		saveWzwj,
		saveQgcjk,
		webpages,
		//保存防护员积分奖励
		savefhyjl,
		//导出防护员当月奖励分配结果
		exportFhyMonthMoney,
		//更新违章违纪数据
		updateWzwj,
		//删除违章违纪数据
		deleteWzwj,
		//批量删除违章违纪数据
		batchDeleteWzwj,
		//删除全过程监控数据
		deleteQgcjk,
		//批量删除全过程监控数据
		batchDeleteQgcjk,
		//导出违章违纪数据
		exportWzwj,
		//导出全过程监控数据
		exportQgcjk,
		//批量导入部门人员数据
		importDeptPerson,
		//获取名字拼音首字母
		getNameFirstChar,
		//导入车间日计划作为全过程监控数据
		importQgcjk,
		//导入评分指标数据
		importPjzbData,
		//修改用户密码
		updateUserPwd
	}
	
	public ApiServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		String ip = SystemUtils.getClientIpAddress(request);
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("msg", "");
		PrintWriter out = response.getWriter();
		String action = request.getParameter("action");
		JSONObject paramJson = HttpRequest.paramMapToJson(request.getParameterMap());
		paramJson.put("ip", ip);
		HttpSession session = request.getSession();
		JSONObject user = (JSONObject)session.getAttribute("user");
		if(user!=null){
			paramJson.put("username", user.getString("username"));
		}
		log.info("请求参数："+paramJson);
		try {
			AjaxMethod method = AjaxMethod.valueOf(action);
			switch (method) {
				case login:
					checkLogin(paramJson, json, request.getSession());
				break;
				case loaddata:
					String sqlkey = paramJson.getString("method");
					String sql = sqlXml.getParamConfig(sqlkey, paramJson);
					log.info("sql="+sql);
					json.put("data", ApplicationListener.dao.listAll(sql));
				break;
				case saveOrUpdate:
					sqlkey = paramJson.getString("method");
					sql = sqlXml.getParamConfig(sqlkey, paramJson);
					log.info("sql="+sql);
					json.put("data", ApplicationListener.dao.saveObject(sql));
					break;
				//查询防护员信息
				case getfhy:
					json = FhyScoreImpl.getFhy(paramJson);
					break;
				case cachedic:
					cacheDicData(sqlXml);
					break;
				case importfhy:
					json = saveFileToDisk(request);
					if(json.getIntValue("code")==0){
						int importCount = FhyScoreImpl.importData(json.getString("filepath"));
						json.put("data", importCount);
					}
					break;
				case uploadHeadimg:
					json = saveFileToDisk(request);
					break;
				case visitlog:
					String countSql = sqlXml.getParamConfig("getVisitLogCount", paramJson);
					int page = paramJson.getIntValue("page");
					int rows = paramJson.getIntValue("rows");
					int total = ApplicationListener.dao.countAll(countSql);
					sql = sqlXml.getParamConfig("getVisitLog", paramJson);
					log.info("sql="+sql);
					List<?> list = ApplicationListener.dao.listAll(sql, page, rows);
					json.put("total", total);
					json.put("rows", list);
					break;
				case useroplog:
					countSql = sqlXml.getParamConfig("getUserOplogCount", paramJson);
					page = paramJson.getIntValue("page");
					rows = paramJson.getIntValue("rows");
					total = ApplicationListener.dao.countAll(countSql);
					sql = sqlXml.getParamConfig("getUserOplog", paramJson);
					log.info("sql="+sql);
					list = ApplicationListener.dao.listAll(sql, page, rows);
					json.put("total", total);
					json.put("rows", list);
					break;
				case pagedata:
					sqlkey = paramJson.getString("method");
					countSql = sqlXml.getParamConfig(sqlkey+"Count", paramJson);
					page = paramJson.getIntValue("page");
					rows = paramJson.getIntValue("rows");
					total = ApplicationListener.dao.countAll(countSql);
					sql = sqlXml.getParamConfig(sqlkey, paramJson);
					log.info("sql="+sql);
					list = ApplicationListener.dao.listAll(sql, page, rows);
					json.put("total", total);
					json.put("rows", list);
					break;
				case savelog:
					saveLog(paramJson,json,request.getSession());
					break;
				case saveWzwj:
					saveWzwj(paramJson,json,request.getSession());
					break;
				case updateWzwj:
					updateWzwj(paramJson,json,request.getSession());
					break;
				case saveQgcjk:
					saveQgcjk(paramJson,json);
					break;
				case webpages:
					JSONArray pages = new JSONArray();
					loadWebPages(pages, ApplicationListener.rootPath);
					json.put("data", pages);
					break;
				case savefhyjl:
					saveFhyMonthMoney(paramJson,json);
					break;
				case exportFhyMonthMoney:
					paramJson.put("year", DateUtils.date2String("yyyy"));
					paramJson.put("month", DateUtils.date2String("MM"));
					//生成奖励分配结果文件
					exportFhyMonthMoney(paramJson,json);
					break;
				case deleteWzwj:
					deleteWzwj(paramJson,json);
					break;
				case batchDeleteWzwj:
					batchDeleteWzwj(paramJson,json);
					break;
				case deleteQgcjk:
					deleteQgcjk(paramJson, json);
					break;
				case batchDeleteQgcjk:
					batchDeleteQgcjk(paramJson,json);
					break;
				case importQgcjk://导入车间日计划作为全过程监控数据
					json = saveFileToDisk(request);
					if(json.getIntValue("code")==0){
						int importCount = FhyScoreImpl.importQgcjkData(json.getString("filepath"),paramJson.getString("username"));
						json.put("data", importCount);
					}
					break;
				case importPjzbData:
					json = saveFileToDisk(request);
					if(json.getIntValue("code")==0){
						int importCount = FhyScoreImpl.importPjzbData(json.getString("filepath"),json.getJSONObject("param").getString("zbid"));
						json.put("data", importCount);
					}
					break;
				case exportWzwj:
					exportWzwj(paramJson, json);
					break;
				case exportQgcjk:
					exportQgcjk(paramJson, json);
					break;
				case importDeptPerson:
					json = saveFileToDisk(request);
					if(json.getIntValue("code")==0){
						int deptid = json.getJSONObject("param").getIntValue("deptid");
						int importCount = importDeptPerson(json.getString("filepath"),deptid);
						json.put("data", importCount);
					}
					break;
				case getNameFirstChar:
					json.put("data", PinyinUtils.getFirstSpell(paramJson.getString("strtext")));
					break;
				case updateUserPwd:
					updateUserPwd(sqlXml,paramJson,json);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			json.put("code", 500);
			json.put("msg", "请求异常："+e.getMessage());
		}
		if(ServerServlet.checktime()>0){
			json.put("code", 107);
			json.put("msg", "系统试用期已到，如需继续使用，请联系系统管理员！");
			json.put("data", "系统试用期已到，如需继续使用，请联系系统管理员！");
		}
		out.print(json);
		out.flush();
		out.close();
	}

	public String getServletInfo() {
		return "This is my default servlet created by Eclipse";
	}

	public void init() throws ServletException {
		
	}
	
	private void updateUserPwd(XmlConfig sqlXml,JSONObject paramJson,JSONObject json){
		String sql = sqlXml.getParamConfig("getUserInfo", paramJson);
		List<?> list = ApplicationListener.dao.listAll(sql);
		if(list.size()>0){
			JSONObject user = (JSONObject)list.get(0);
			String userpwd = user.getString("userpwd");
			if(userpwd.equals(paramJson.getString("oldpwd"))){
				sql = sqlXml.getParamConfig("updateUserPwd", paramJson);
				int ret = ApplicationListener.dao.updateObject(sql);
				json.put("data", ret);
			}else{
				json.put("code", 102);
				json.put("msg", "用户账号原密码不正确！");
			}
		}else{
			json.put("code", 101);
			json.put("msg", "用户账号不存在，修改密码失败");
		}
	}
	
	/**
	 * 将字典数据缓存到文件中.<br>
	 * @param sqlXml
	 * @return void
	 */
	private void cacheDicData(XmlConfig sqlXml){
		String sql = sqlXml.getConfigValue("getAllDic");
		List<?> dicdata = ApplicationListener.dao.listAll(sql);
		JSONObject json = new JSONObject();
		json.put("dicdata", dicdata);
		JSONArray dataArray = json.getJSONArray("dicdata");
		String jscontent = "var dicdata="+dataArray.toJSONString()+";";
		File file = new File(ApplicationListener.rootPath+"/jslib/dicdata.js");
		File jsonfile = new File(ApplicationListener.rootPath+"/jslib/dicdata.json");
		try {
			FileUtils.writeStringToFile(file, jscontent, "UTF-8");
			FileUtils.writeStringToFile(jsonfile, dataArray.toJSONString(), "UTF-8");
		} catch (IOException e) {
			log.error("IOException", new Throwable(e));
			//e.printStackTrace();
		}
	}
	
	private JSONObject saveFileToDisk(HttpServletRequest request){
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("msg", "");
		DiskFileItemFactory fac = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fac);
		//设置字符编码，避免中文乱码
		upload.setHeaderEncoding("UTF-8");
		//存储前台传入的form-data参数
		JSONObject paramJson = new JSONObject();
		//上传的文件对象
		FileItem uploadFile = null;
		try {
			//设置接收的编码格式      
			request.setCharacterEncoding("UTF-8");
			//获取多个上传文件
			List<?> items = upload.parseRequest(request);
			log.info("itemCount="+items.size());
			FileItem fileItem = null;
			for(Object obj:items){
				fileItem = (FileItem)obj;
				if(fileItem.isFormField()){//判断是否为form-data参数，前台传入的参数
					paramJson.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
				}else{
					uploadFile = fileItem;
				}
			}
			log.info("前台传入的参数为："+paramJson.toJSONString());
			String fileFullName = uploadFile.getName();
			JSONObject finfo = getFileInfo(fileFullName);
			String contentType = uploadFile.getContentType();
			System.out.println("contentType="+contentType);
			String filetype = finfo.getString("filetype");
			System.out.println("filetype="+filetype);
			//生成随机文件名称
			String newFileName = "file-"+DateUtils.date2String("yyyyMMddHHmmssSSS")+"."+filetype;
			//上传文件存储总目录
			String uploadFolder = ApplicationListener.rootPath;
			String absolutePath = "";
			absolutePath = "upload/"+newFileName;
			String filepath = uploadFolder+absolutePath;
			File file = new File(filepath);
			FileUtils.forceMkdir(file.getParentFile());
			FileUtils.copyInputStreamToFile(uploadFile.getInputStream(), file);
			json.put("filepath", filepath);
			json.put("absolutePath", absolutePath);
			json.put("param", paramJson);
			//将参数放到请求参数JSON对象中
		} catch (UnsupportedEncodingException e) {
			json.put("code", "501");
			json.put("msg", "转换编码出现异常！"+e.getMessage());
			log.error("UnsupportedEncodingException", new Throwable(e));
		} catch (FileUploadException e) {
			json.put("code", "502");
			json.put("msg", "文件上传出现异常！"+e.getMessage());
			log.error("UnsupportedEncodingException", new Throwable(e));
		} catch (IOException e) {
			json.put("code", "503");
			json.put("msg", "文件读写出现异常！"+e.getMessage());
			log.error("UnsupportedEncodingException", new Throwable(e));
		}
		return json;
	}
	
	private JSONObject getFileInfo(String filename){
		JSONObject info = new JSONObject();
		if(filename!=null&&!filename.equals("")){
			int index = filename.lastIndexOf(".");
			String name = filename.substring(0,index);
			String type = filename.substring(index+1,filename.length());
			info.put("filename", name);
			info.put("filetype", type);
		}
		return info;
	}
	
	private void checkLogin(JSONObject paramJson,JSONObject json,HttpSession session){
		XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getParamConfig("checkLogin", paramJson);
		log.info("sql="+sql);
		JSONObject user = (JSONObject)ApplicationListener.dao.loadObject(sql);
		String userpwd = paramJson.getString("userpwd"); 
		if(user==null){
			json.put("code", 101);
			json.put("msg", "登录用户名不存在！");
		}else{
			String password = user.get("userpwd").toString();
			if(password.equals(userpwd)){
				user.remove("userpwd");
				String pageright = user.getString("pageright");
				//获取用户可以访问的页面
				String userpages = getUserPages(pageright);
				user.put("pagelist", userpages);
				session.setAttribute("user", user);
				json.put("data", user);
			}else{
				json.put("code", 102);
				json.put("msg", "登录用户密码不正确！");
			}
		}
	}
	/**
	 * 根据用户的权限获取可以访问的页面.<br>
	 * @param pageright 菜单页面编码集合
	 * @return String 可以访问的页面
	 */
	private String getUserPages(String pageright){
		String[] pageIds = pageright.split("[,]");
		List<String> pageIdList = Arrays.asList(pageIds);
		List<?> pageData = ApplicationListener.loadPageData();
		int count = pageData.size();
		JSONObject record = null;
		String id = "";
		List<String> pageList = new ArrayList<String>();
		String pageurl = "";
		for(int i=0;i<count;i++){
			record = (JSONObject)pageData.get(i);
			id = record.getString("id");
			pageurl = record.getString("pageurl");
			if(pageIdList.contains(id) && pageurl.length()>0){
				pageList.add(pageurl);
			}
		}
		String[] pageArr = new String[pageList.size()];
		pageList.toArray(pageArr);
		String userPages = StringUtils.join(pageArr,",");
		return userPages;
	}
	
	private void saveLog(JSONObject paramJson,JSONObject json,HttpSession session){
		XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getParamConfig("saveUserOplog", paramJson);
		ApplicationListener.dao.saveObject(sql);
	}
	
	private void saveWzwj(JSONObject paramJson,JSONObject json,HttpSession session){
		long wzwjbh = RandomUtils.genItemId();
		paramJson.put("wzwjbh", wzwjbh);
		XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getParamConfig("addWzwj", paramJson);
		int ret = ApplicationListener.dao.saveObject(sql);
		json.put("data", ret);
		if(ret>0){
			//保存违章发现人数据到关联表中
			String fxrlist = paramJson.getString("fxrlist");
			String[] tArr = fxrlist.split("[|]");
			String[] arr = null;
			JSONObject sqlParam = new JSONObject();
			sqlParam.put("wzwjbh", wzwjbh);
			for(int i=0;i<tArr.length;i++){
				arr = tArr[i].split("[,]");
				sqlParam.put("fxdwid", arr[0]);
				sqlParam.put("fxrid", arr[1]);
				sql = xml.getParamConfig("addWzwjFxr", sqlParam);
				ApplicationListener.dao.saveObject(sql);
			}
			//保存违章违纪防护员减分信息表
			String[] pArr = paramJson.getString("zyzrr").split("[,]");
			String[] zrrArr = paramJson.getString("zyzrrinfo").split("[,]");
			float score = getScoreByWzxz(paramJson.getString("wzxz"));
			String wzwjrq = paramJson.getString("wzwjrq");
			String[] dateArr = wzwjrq.split("[-]");
			sqlParam.put("wzwjrq", wzwjrq);
			sqlParam.put("score", score);
			sqlParam.put("year", dateArr[0]);
			sqlParam.put("month", dateArr[1]);
			sqlParam.put("date", dateArr[2]);
			sqlParam.put("jfbh", wzwjbh);
			sqlParam.put("jiafen", 0);
			sqlParam.put("jianfen", score);
			String zrrInfo = null;
			String isfhy = "0";
			for(int i=0;i<pArr.length;i++){
				zrrInfo = zrrArr[i];
				if(zrrInfo.split("[_]")[0].equals("0")){
					isfhy = "0";
					sqlParam.put("score", "0");
				}else{
					isfhy = "1";
					sqlParam.put("score", score);
				}
				sqlParam.put("isfhy", isfhy);
				sqlParam.put("fhybh", pArr[i]);
				sql = xml.getParamConfig("addWzwjFhy", sqlParam);
				ApplicationListener.dao.saveObject(sql);
				if(isfhy.equals("1")){
					saveFhyScore(String.valueOf(wzwjbh), wzwjrq, pArr[i], "0", String.valueOf(score));
				}
				//scoreSql = xml.getParamConfig("saveFhyScore", sqlParam);
				//sql = "insert into T_WZWJ_FHY(wzwjbh,wzwjrq,fhyxm,score) values('"+wzwjbh+"','"+wzwjrq+"','"+pArr[i]+"','"+score+"')";
				//scoreSql = "insert into T_FHY_SCORE(jfbh,year,month,date,fhyxm,jiafen,jianfen) values('"+wzwjbh+"','"+dateArr[0]+"','"+dateArr[1]+"','"+dateArr[2]+"','"+pArr[i]+"','0','"+score+"')";
				//ApplicationListener.dao.saveObject(scoreSql);
			}
		}
	}
	
	private void updateWzwj(JSONObject paramJson,JSONObject json,HttpSession session){
		String wzwjbh = paramJson.getString("wzwjbh");
		XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getParamConfig("updateWzwj", paramJson);
		int ret = ApplicationListener.dao.saveObject(sql);
		json.put("data", ret);
		if(ret>0){
			//先删除发现人关联信息
			ApplicationListener.dao.updateObject(xml.getParamConfig("deleteWzwjFxr", paramJson));
			//保存违章发现人数据到关联表中
			String fxrlist = paramJson.getString("fxrlist");
			String[] tArr = fxrlist.split("[|]");
			String[] arr = null;
			JSONObject sqlParam = new JSONObject();
			sqlParam.put("wzwjbh", wzwjbh);
			for(int i=0;i<tArr.length;i++){
				arr = tArr[i].split("[,]");
				sqlParam.put("fxdwid", arr[0]);
				sqlParam.put("fxrid", arr[1]);
				sql = xml.getParamConfig("addWzwjFxr", sqlParam);
				ApplicationListener.dao.saveObject(sql);
			}
			//先删除防护员积分关联信息
			JSONObject param = new JSONObject();
			param.put("jfbh", wzwjbh);
			ApplicationListener.dao.updateObject(xml.getParamConfig("deleteFhyScore", param));
			ApplicationListener.dao.updateObject(xml.getParamConfig("deleteWzwjFhy", paramJson));
			//保存违章违纪防护员减分信息表
			String[] pArr = paramJson.getString("zyzrr").split("[,]");
			String[] zrrArr = paramJson.getString("zyzrrinfo").split("[,]");
			float score = getScoreByWzxz(paramJson.getString("wzxz"));
			String wzwjrq = paramJson.getString("wzwjrq");
			String[] dateArr = wzwjrq.split("[-]");
			sqlParam.put("wzwjrq", wzwjrq);
			sqlParam.put("score", score);
			sqlParam.put("year", dateArr[0]);
			sqlParam.put("month", dateArr[1]);
			sqlParam.put("date", dateArr[2]);
			sqlParam.put("jfbh", wzwjbh);
			sqlParam.put("jiafen", 0);
			sqlParam.put("jianfen", score);
			String zrrInfo = null;
			String isfhy = "0";
			for(int i=0;i<pArr.length;i++){
				zrrInfo = zrrArr[i];
				if(zrrInfo.split("[_]")[0].equals("0")){
					isfhy = "0";
					sqlParam.put("score", "0");
				}else{
					isfhy = "1";
					sqlParam.put("score", score);
				}
				sqlParam.put("isfhy", isfhy);
				sqlParam.put("fhybh", pArr[i]);
				sql = xml.getParamConfig("addWzwjFhy", sqlParam);
				ApplicationListener.dao.saveObject(sql);
				if(isfhy.equals("1")){
					saveFhyScore(String.valueOf(wzwjbh), wzwjrq, pArr[i], "0", String.valueOf(score));
				}
				//scoreSql = xml.getParamConfig("saveFhyScore", sqlParam);
				//sql = "insert into T_WZWJ_FHY(wzwjbh,wzwjrq,fhyxm,score) values('"+wzwjbh+"','"+wzwjrq+"','"+pArr[i]+"','"+score+"')";
				//scoreSql = "insert into T_FHY_SCORE(jfbh,year,month,date,fhyxm,jiafen,jianfen) values('"+wzwjbh+"','"+dateArr[0]+"','"+dateArr[1]+"','"+dateArr[2]+"','"+pArr[i]+"','0','"+score+"')";
				//ApplicationListener.dao.saveObject(scoreSql);
			}
		}
	}
	/**
	 * 根据违章性质计算扣分
	 * 请用一句话描述这个方法实现的功能.<br>
	 * @param wzxz 违章性质
	 * @return float 扣分值
	 */
	public float getScoreByWzxz(String wzxz){
		XmlConfig xml = new XmlConfig(ApplicationListener.webconfigFilePath);
		String scoreConfig = xml.getConfigValue("wzxzScore");
		String[] strArr = scoreConfig.split("[|]");
		String[] tArr = null;
		String score = null;
		for(int i=0;i<strArr.length;i++){
			tArr = strArr[i].split("[:]");
			if(tArr[0].equals(wzxz)){
				score = tArr[1];
				break;
			}
		}
		return Float.parseFloat(score);
	}
	/**
	 * 保存全过程监控信息.<br>
	 * @param paramJson
	 * @param json
	 */
	private void saveQgcjk(JSONObject paramJson,JSONObject json){
		XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = "";
		int ret = 0;
		//如果ID为空则为新增数据
		if(paramJson.getString("id").equals("")){
			paramJson.put("qgcjkbh", RandomUtils.genItemId());
			sql = xml.getParamConfig("saveQgcjk", paramJson);
			ret = ApplicationListener.dao.saveObject(sql);
		}else{//如果ID不为空则为修改数据，修改基础数据，删除积分数据重新计算积分
			sql = xml.getParamConfig("editQgcjk", paramJson);
			ret = ApplicationListener.dao.updateObject(sql);
			if(ret>0){
				sql = xml.getParamConfig("deleteQgcjkFhy", paramJson);
				ApplicationListener.dao.updateObject(sql);
				paramJson.put("jfbh", paramJson.getString("qgcjkbh"));
				sql = xml.getParamConfig("deleteFhyScore", paramJson);
				ApplicationListener.dao.updateObject(sql);
			}
		}
		json.put("data", ret);
		if(ret>0){
			//保存监控过程中防护员到关联表中
			String zz = paramJson.getString("zz");
			String xc = paramJson.getString("xc");
			String yd = paramJson.getString("yd");
			String fxyd = paramJson.getString("fxyd");
			String zzdgsj = paramJson.getString("zzdgsj");
			String zzlgsj = paramJson.getString("zzlgsj");
			String sjrwsj = paramJson.getString("sjrwsj");
			String sjcwsj = paramJson.getString("sjcwsj");
			float score = 0.0f;
			List<?> list = new ArrayList();
			//如果是驻站、远端和反向远端，则需要判断时间段，时间段相同只计算一次
			if(xc !=null && !xc.equals("")){//如果有数据才进行计算，未选择则不进行计算
				//如果是现场则直接计算
				score = getScoreByQgcjk(xc, sjrwsj, sjcwsj);
				paramJson.put("fhylb", "xc");
				paramJson.put("fhybh", xc);
				paramJson.put("stime", sjrwsj);
				paramJson.put("etime", sjcwsj);
				paramJson.put("score", score);
				sql = xml.getParamConfig("saveQgcjkFhy", paramJson);
				log.info("saveQgcjkFhy[sql]="+sql);
				ApplicationListener.dao.saveObject(sql);
				saveFhyScore(paramJson.getString("qgcjkbh"),paramJson.getString("zyrq"),xc,String.valueOf(score),"0");
			}
			//计算驻站得分
			if(zz != null && !zz.equals("")){//如果有数据才进行计算，未选择则不进行计算
				paramJson.put("fhylb", "zz");
				paramJson.put("fhybh", zz);
				paramJson.put("stime", zzdgsj);
				paramJson.put("etime", zzlgsj);
				sql = xml.getParamConfig("queryQgcjkScoreExist", paramJson);
				log.info("queryQgcjkScoreExist[sql]="+sql);
				list = ApplicationListener.dao.listAll(sql);
				if(list.size()==0){//不存在则直接计算得分
					score = getScoreByQgcjk(zz, zzdgsj, zzlgsj);
					saveFhyScore(paramJson.getString("qgcjkbh"),paramJson.getString("zyrq"),zz,String.valueOf(score),"0");
				}else{//已存在，得分为0
					score = 0.0f;
				}
				paramJson.put("score", score);
				sql = xml.getParamConfig("saveQgcjkFhy", paramJson);
				log.info("saveQgcjkFhy[zzsql]="+sql);
				ApplicationListener.dao.saveObject(sql);
			}
			//计算远端得分
			if(yd != null && !yd.equals("")){//如果有数据才进行计算，未选择则不进行计算
				paramJson.put("fhylb", "yd");
				paramJson.put("fhybh", yd);
				paramJson.put("stime", sjrwsj);
				paramJson.put("etime", sjcwsj);
				sql = xml.getParamConfig("queryQgcjkScoreExist", paramJson);
				log.info("queryQgcjkScoreExist[ydsql]="+sql);
				list = ApplicationListener.dao.listAll(sql);
				if(list.size()==0){//不存在则直接计算得分
					score = getScoreByQgcjk(yd, sjrwsj, sjcwsj);
					saveFhyScore(paramJson.getString("qgcjkbh"),paramJson.getString("zyrq"),yd,String.valueOf(score),"0");
				}else{//已存在，得分为0
					score = 0.0f;
				}
				paramJson.put("score", score);
				sql = xml.getParamConfig("saveQgcjkFhy", paramJson);
				log.info("saveQgcjkFhy[ydsql]="+sql);
				ApplicationListener.dao.saveObject(sql);
			}
			//计算反向远端得分
			if(fxyd != null && !fxyd.equals("")){//如果有数据才进行计算，未选择则不进行计算
				paramJson.put("fhylb", "fxyd");
				paramJson.put("fhybh", fxyd);
				paramJson.put("stime", sjrwsj);
				paramJson.put("etime", sjcwsj);
				sql = xml.getParamConfig("queryQgcjkScoreExist", paramJson);
				log.info("queryQgcjkScoreExist[fxydsql]="+sql);
				list = ApplicationListener.dao.listAll(sql);
				if(list.size()==0){//不存在则直接计算得分
					score = getScoreByQgcjk(fxyd, sjrwsj, sjcwsj);
					saveFhyScore(paramJson.getString("qgcjkbh"),paramJson.getString("zyrq"),fxyd,String.valueOf(score),"0");
				}else{//已存在，得分为0
					score = 0.0f;
				}
				paramJson.put("score", score);
				sql = xml.getParamConfig("saveQgcjkFhy", paramJson);
				log.info("saveQgcjkFhy[fxydsql]="+sql);
				ApplicationListener.dao.saveObject(sql);
			}
		}
	}
	/**
	 * 保存防护员积分数据.<br>
	 * @param date 日期
	 * @param fhybh 防护员编号
	 * @param jiafen 加分值
	 * @param jianfen 减分值
	 * @return void
	 */
	public void saveFhyScore(String jfbh,String date,String fhybh,String jiafen,String jianfen){
		String[] dateArr = date.split("[-]");
		XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		JSONObject sqlParam = new JSONObject();
		sqlParam.put("jfbh", jfbh);
		sqlParam.put("year", dateArr[0]);
		sqlParam.put("month", dateArr[1]);
		sqlParam.put("date", dateArr[2]);
		sqlParam.put("fhybh", fhybh);
		sqlParam.put("jiafen", jiafen);
		sqlParam.put("jianfen", jianfen);
		String scoreSql = xml.getParamConfig("saveFhyScore", sqlParam);
		//String scoreSql = "insert into T_FHY_SCORE(jfbh,year,month,date,fhyxm,jiafen,jianfen) values('"+dateArr[0]+"','"+dateArr[1]+"','"+dateArr[2]+"','"+fhyxm+"','"+jiafen+"','"+jianfen+"')";
		int ret = ApplicationListener.dao.saveObject(scoreSql);
		if(ret==1){
			log.info("防护员积分数据添加成功!");
		}else{
			log.info("防护员积分数据添加失败!");
		}
	}
	
	/**
	 * 计算防护员加分值.<br>
	 * @param fhyxm 防护员姓名
	 * @param starttime 开始时间
	 * @param endtime 结束时间
	 * @return float 加分值
	 */
	public float getScoreByQgcjk(String fhyxm,String starttime,String endtime){
		float score = 0.0f;
		String[] stimeArr = starttime.split("[:]");
		String[] etimeArr = endtime.split("[:]");
		int start_minute = Integer.parseInt(stimeArr[0])*60 + Integer.parseInt(stimeArr[1]);
		int end_minute = Integer.parseInt(etimeArr[0])*60 + Integer.parseInt(etimeArr[1]);
		int space_minute = end_minute-start_minute;
		score = space_minute/(4*60.0F);
		return score;
	}
	/**
	 * 加载所有的页面.<br>
	 * @param data
	 * @param folderpath
	 */
	public void loadWebPages(JSONArray data,String folderpath){
		File file = new File(folderpath);
		if(file.exists()){
			File[] files = file.listFiles();
			File f = null;
			String filepath = null;
			JSONObject json = new JSONObject();
			for(int i=0;i<files.length;i++){
				f = files[i];
				filepath = f.getAbsolutePath().replaceAll("\\\\", "/");
				filepath = filepath.replaceAll(ApplicationListener.rootPath, "");
				if(f.isFile()){
					if(f.getName().contains(".htm")){
						json = new JSONObject();
						json.put("val", filepath);
						json.put("text", filepath);
						data.add(json);
					}
				}else{
					loadWebPages(data, f.getAbsolutePath());
				}
			}
		}
	}
	/**
	 * 保存防护员当月奖励.<br>
	 * @param paramJson
	 * @param json
	 */
	public void saveFhyMonthMoney(JSONObject paramJson,JSONObject json){
		String year = paramJson.getString("year");
		String month = paramJson.getString("month");
		String data = paramJson.getString("data");
		String[] tArr = data.split("[|]");
		XmlConfig xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = "";
		JSONObject sqlParam = new JSONObject();
		sqlParam.put("year", year);
		sqlParam.put("month", month);
		//先清空当月数据
		sql = xml.getParamConfig("deleteMonthFhyMoney", sqlParam);
		ApplicationListener.dao.updateObject(sql);
		String[] vArr = null;
		List<String> sqlList = new ArrayList<String>();
		for(int i=0;i<tArr.length;i++){
			vArr = tArr[i].split("[,]");
			sqlParam.put("fhybh", vArr[0]);
			sqlParam.put("dyjl", vArr[1]);
			sql = xml.getParamConfig("saveMonthFhyMoney", sqlParam);
			sqlList.add(sql);
		}
		int result = ApplicationListener.dao.betchSaveData(sqlList);
		json.put("data", result);
	}
	
	@SuppressWarnings("unchecked")
	public void exportWzwj(JSONObject paramJson,JSONObject json){
		XmlConfig webconfig = new XmlConfig(ApplicationListener.webconfigFilePath);
		String fields = webconfig.getConfigValue("wxwjExportField");
		String absolutePath = "temp/违章违纪数据-"+DateUtils.date2String("yyyyMMddHHmmss")+".xls";
		String filepath = ApplicationListener.rootPath+absolutePath;
		String sqlkey = paramJson.getString("method");
		XmlConfig  xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getParamConfig(sqlkey, paramJson);
		List<JSONObject> dataList = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		try {
			Workbook wb = new HSSFWorkbook();
			String sheetName = "违章违纪";
			Sheet sheet = wb.createSheet(sheetName);
			ExcelUtils utils = new ExcelUtils(wb, sheet);
			utils.export(filepath, sheetName, fields, dataList);
			log.info("共导出["+dataList.size()+"]条数据！");
			json.put("data", absolutePath);
		} catch (Exception e) {
			log.error("Exception", new Throwable(e));
			json.put("code", 500);
			json.put("msg", "导出数据出现异常："+e.getMessage());
			//e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void exportQgcjk(JSONObject paramJson,JSONObject json){
		XmlConfig webconfig = new XmlConfig(ApplicationListener.webconfigFilePath);
		String fields = webconfig.getConfigValue("qgcjkExportField");
		String absolutePath = "temp/全过程监控数据-"+DateUtils.date2String("yyyyMMddHHmmss")+".xls";
		String filepath = ApplicationListener.rootPath+absolutePath;
		String sqlkey = paramJson.getString("method");
		XmlConfig  xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getParamConfig(sqlkey, paramJson);
		List<JSONObject> dataList = (List<JSONObject>)ApplicationListener.dao.listAll(sql);
		try {
			Workbook wb = new HSSFWorkbook();
			String sheetName = "全过程监控";
			Sheet sheet = wb.createSheet(sheetName);
			ExcelUtils utils = new ExcelUtils(wb, sheet);
			utils.export(filepath, sheetName, fields, dataList);
			log.info("共导出["+dataList.size()+"]条数据！");
			json.put("data", absolutePath);
		} catch (Exception e) {
			log.error("Exception", new Throwable(e));
			json.put("code", 500);
			json.put("msg", "导出数据出现异常："+e.getMessage());
			//e.printStackTrace();
		}
	}
	
	public void exportFhyMonthMoney(JSONObject paramJson,JSONObject json){
		String tempFilepath = ApplicationListener.rootPath+"template/防护员当月奖励分配结果-Template.xls";
		String month = DateUtils.date2String("yyyy年MM月");
		String absolutePath = "temp/防护员当月奖励分配结果-"+month+".xls";
		String filepath = ApplicationListener.rootPath+absolutePath;
		Workbook wb;
		try {
			wb = ExcelUtils.getWorkbook(tempFilepath);
			Sheet sheet = wb.getSheetAt(0);
			ExcelUtils utils = new ExcelUtils(wb, sheet);
			XmlConfig  xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
			String sql = xml.getParamConfig("exportFhyMonthMoney", paramJson);
			List<?> datalist = ApplicationListener.dao.listAll(sql);
			XmlConfig webconfig = new XmlConfig(ApplicationListener.webconfigFilePath);
			String fields = webconfig.getConfigValue("fhyMonthMoneyExportField");
			List<String> fieldList = Arrays.asList(fields.split(","));
			utils.exportByTemplate(filepath, 0, datalist, fieldList);
			json.put("data", absolutePath);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException", new Throwable(e));
			//e.printStackTrace();
		} catch (IOException e) {
			log.error("IOException", new Throwable(e));
			//e.printStackTrace();
		} catch (Exception e) {
			log.error("Exception", new Throwable(e));
			//e.printStackTrace();
		}
	}
	/**
	 * 删除违章违纪数据.<br>
	 * @param paramJson 
	 * @param json
	 * @return void
	 */
	public void deleteWzwj(JSONObject paramJson,JSONObject json){
		XmlConfig  xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getParamConfig("deleteWzwj", paramJson);
		int ret = ApplicationListener.dao.updateObject(sql);
		sql = xml.getParamConfig("deleteWzwjFhy", paramJson);
		ApplicationListener.dao.updateObject(sql);
		sql = xml.getParamConfig("deleteWzwjFxr", paramJson);
		ApplicationListener.dao.updateObject(sql);
		sql = xml.getParamConfig("deleteFhyScore", paramJson);
		ApplicationListener.dao.updateObject(sql);
		json.put("data", ret);
	}
	/**
	 * @Description 批量删除违章违纪数据.<br>
	 * @author 雷志强  2018年12月25日 下午11:43:45
	 * @param paramJson
	 * @param json
	 * @return void
	 */
	public void batchDeleteWzwj(JSONObject paramJson,JSONObject json){
		String ids = paramJson.getString("ids");
		log.info("要删除的数据编号："+ids);
		String[] idArr = ids.split("[,]");
		int count = idArr.length;
		log.info("要删除的数据条数："+count);
		XmlConfig  xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		JSONObject sqlParam = null;
		String sql = null;
		int okstate = 0;
		int okcount = 0;
		for(int i=0;i<count;i++){
			sqlParam = new JSONObject();
			sql = xml.getParamConfig("deleteWzwj", sqlParam);
			okstate = ApplicationListener.dao.updateObject(sql);
			if(okstate > 0){
				okcount += okstate;
				sql = xml.getParamConfig("deleteWzwjFhy", sqlParam);
				ApplicationListener.dao.updateObject(sql);
				sql = xml.getParamConfig("deleteWzwjFxr", sqlParam);
				ApplicationListener.dao.updateObject(sql);
				sql = xml.getParamConfig("deleteFhyScore", sqlParam);
				ApplicationListener.dao.updateObject(sql);
				log.info("删除数据【"+idArr[i]+"】成功！");
			}else{
				log.info("删除数据【"+idArr[i]+"】失败！");
			}
		}
		log.info("成功删除的数据条数："+okcount);
		json.put("data", okcount);
	}
	
	/**
	 * 删除全过程监控数据.<br>
	 * @param paramJson
	 * @param json
	 * @return void
	 */
	public void deleteQgcjk(JSONObject paramJson,JSONObject json){
		XmlConfig  xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = xml.getParamConfig("deleteQgcjk", paramJson);
		int ret = ApplicationListener.dao.updateObject(sql);
		if(ret==1){
			sql = xml.getParamConfig("deleteQgcjkFhy", paramJson);
			ApplicationListener.dao.updateObject(sql);
			sql = xml.getParamConfig("deleteFhyScore", paramJson);
			ApplicationListener.dao.updateObject(sql);
		}
		json.put("data", ret);
	}
	
	/**
	 * @Description 批量删除全过程监控数据.<br>
	 * @author 雷志强  2018年12月25日 下午11:38:37
	 * @param paramJson 请求参数，必须要有ids参数，用逗号隔开
	 * @param json 
	 * @return void
	 */
	public void batchDeleteQgcjk(JSONObject paramJson,JSONObject json){
		XmlConfig  xml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String ids = paramJson.getString("ids");
		log.info("要删除的数据编号："+ids);
		String[] idArr = ids.split(",");
		int count = idArr.length;
		log.info("要删除的数据条数："+count);
		JSONObject sqlParam = new JSONObject();
		int okcount = 0;
		String sql = null;
		int okstate = 0;
		for(int i=0;i<count;i++){
			sqlParam = new JSONObject();
			sqlParam.put("qgcjkbh", idArr[i]);
			sqlParam.put("jfbh", idArr[i]);
			sql = xml.getParamConfig("deleteQgcjk", sqlParam);
			okstate = ApplicationListener.dao.updateObject(sql);
			if(okstate > 0){
				okcount += okstate;
				sql = xml.getParamConfig("deleteQgcjkFhy", sqlParam);
				ApplicationListener.dao.updateObject(sql);
				sql = xml.getParamConfig("deleteFhyScore", sqlParam);
				ApplicationListener.dao.updateObject(sql);
				log.info("删除数据【"+idArr[i]+"】成功！");
			}else{
				log.info("删除数据【"+idArr[i]+"】失败！");
			}
		}
		log.info("成功删除的数据条数："+okcount);
		json.put("data", okcount);
	}
	
	/**
	 * 导入部门人员信息.<br>
	 * @param filepath 上传数据表格文件路径
	 * @param deptid 部门ID
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return int
	 */
	public static int importDeptPerson(String filepath,int deptid) throws FileNotFoundException, IOException{
		int successCount = 0;
		File file = new File(filepath);
		if(file.exists()){
			Workbook workbook = ExcelUtils.getWorkbook(filepath);
			Sheet sheet = workbook.getSheetAt(0);
			ExcelUtils utils = new ExcelUtils(workbook,sheet);
			List<List<String>> dataList = utils.getSheetData(sheet);
			if(dataList != null){
				int count = dataList.size();
				log.info("表格数据总行数："+count);
				long st = System.currentTimeMillis();
				XmlConfig xml = new XmlConfig(ApplicationListener.webconfigFilePath);
				String fields = xml.getConfigValue("personExcelField");
				String[] fieldArr = fields.split("[|]");
				int fieldCount = fieldArr.length;
				List<String> rowList = null;
				JSONObject personJson = null;
				JSONObject voJson = null;
				HibernateDAOImpl dao = new HibernateDAOImpl();
				PersonVo vo = null;
				List<Object> personList = new ArrayList<Object>();
				for(int i=2;i<count;i++){
					personJson = new JSONObject();
					rowList = dataList.get(i);
					if(rowList.size()==fieldCount){
						for(int k=0;k<fieldCount;k++){
							personJson.put(fieldArr[k], rowList.get(k));
						}
					}
					personJson.put("namepy", PinyinUtils.getFirstSpell(personJson.getString("name")));
					personJson.put("deptid", deptid);
					personJson.put("state", 1);
					personJson.put("headimg", "");
					personJson.put("description", "");
					vo = (PersonVo)dao.loadObject("from PersonVo where id='"+personJson.getString("id")+"'");
					if(vo == null){
						vo = JSON.toJavaObject(personJson, PersonVo.class);
					}else{
						voJson = JSONObject.parseObject(JSONObject.toJSONString(vo));
						voJson.putAll(personJson);
						voJson.put("edittime", DateUtils.date2String("yyyy-MM-dd HH:mm:ss"));
						vo = JSON.toJavaObject(voJson, PersonVo.class);
					}
					personList.add(vo);
				}
				successCount = dao.bulkSaveOrUpdate(personList);
				long et = System.currentTimeMillis();
				log.info("成功导入["+successCount+"]条数据！共耗时：("+(et-st)+"ms)");
			}
		}else{
			log.warn("文件不存在！"+filepath);
		}
		return successCount;
	}
	/**
	 * 生成部门人员批量导入数据表格模板文件.<br>
	 */
	public static void createDeptPersonExcelTemplate(){
		String filepath = "D:/JavaWorkSpaces/fhy/WebRoot/template/人员信息表-Template.xls";
		Workbook workbook = null;
		try {
			XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
			XmlConfig webXml = new XmlConfig(ApplicationListener.webconfigFilePath);
			String sql = sqlXml.getConfigValue("getAllDic");
			List<?> dicdata = ApplicationListener.dao.listAll(sql);
			workbook = ExcelUtils.getWorkbook(filepath);
			Sheet sheet = workbook.getSheetAt(0);
			ExcelUtils utils = new ExcelUtils(workbook,sheet);
			Row headRow = sheet.getRow(1);
			int cellCount = headRow.getPhysicalNumberOfCells();
			Cell cell = null;
			String cellvalue = null;
			StringBuffer sb = new StringBuffer();
			JSONObject dic = null;
			short firstRow = 2;
			short endRow = Short.parseShort(webXml.getConfigValue("importExcelRowCount"));
			short firstCol = 0;
			short endCol = 0;
			String text = null;
			String tips = "请从列表中选择";
			HSSFDataValidation cellValidate = null;
			for(int i=0;i<cellCount;i++){
				firstCol = (short)i;
				endCol = firstCol;
				cell = headRow.getCell(i);
				cellvalue = utils.getCellValue(cell);
				sb = new StringBuffer();
				for(int k=0;k<dicdata.size();k++){
					dic = (JSONObject)dicdata.get(k);
					if(dic.getString("dicname").equals(cellvalue)){
						sb.append(dic.getString("name")).append(",");
					}
				}
				text = sb.toString();
				if(text.length()>1){
					text = text.substring(0,text.length()-1);
					cellValidate = utils.setDataValidationList(firstRow, endRow, firstCol, endCol, text, tips+cellvalue);
					sheet.addValidationData(cellValidate);
				}
			}
			utils.outputExcel(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//testExportQgcjk();
		//testImportDeptPerson();
		testBatchDeleteQgcjk();
		//createDeptPersonExcelTemplate();
		System.out.println("OK");
	}
	
	public static void testBatchDeleteQgcjk(){
		ApiServlet servlet = new ApiServlet();
		JSONObject paramJson = new JSONObject();
		paramJson.put("ids", "154574848411958,154574848412122,154574848412488,154574848412806,154574848413274,154574848413677,154574848413850,154574848414760,154574848415066,154574848415387");
		JSONObject json = new JSONObject();
		servlet.batchDeleteQgcjk(paramJson, json);
		System.out.println(json);
	}
	
	public static void testLoadPages(){
		ApiServlet servlet = new ApiServlet();
		JSONArray data = new JSONArray();
		String rootPath = ApplicationListener.rootPath;
		servlet.loadWebPages(data,rootPath);
		System.out.println(data);
	}
	
	public static void test222(){
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		ApiServlet servlet = new ApiServlet();
		servlet.cacheDicData(sqlXml);
	}
	
	public static void test2(){
		ApiServlet servlet = new ApiServlet();
		float score = servlet.getScoreByQgcjk("张三丰", "09:00", "18:00");
		System.out.println(score);
	}
	
	public static void test1(){
		ApiServlet servlet = new ApiServlet();
		float score = servlet.getScoreByWzxz("设备红线");
		System.out.println(score);
	}
	
	public static void testExportFhyMonthMoney(){
		ApiServlet servlet = new ApiServlet();
		JSONObject paramJson = new JSONObject();
		paramJson.put("year", DateUtils.date2String("yyyy"));
		paramJson.put("month", DateUtils.date2String("MM"));
		JSONObject json = new JSONObject();
		servlet.exportFhyMonthMoney(paramJson,json);
	}
	
	public static void testDeleteWzwj(){
		ApiServlet servlet = new ApiServlet();
		JSONObject paramJson = new JSONObject();
		paramJson.put("wzwjbh", "153797850404148");
		paramJson.put("jfbh", "153797850404148");
		JSONObject json = new JSONObject();
		servlet.deleteWzwj(paramJson, json);
	}
	
	public static void testExportWzwj(){
		ApiServlet servlet = new ApiServlet();
		JSONObject paramJson = new JSONObject();
		paramJson.put("method", "importDeptPerson");
		paramJson.put("filepath", "");
		//paramJson.put("deptid", "");
		JSONObject json = new JSONObject();
		servlet.exportWzwj(paramJson, json);
	}
	
	public static void testExportQgcjk(){
		ApiServlet servlet = new ApiServlet();
		JSONObject paramJson = new JSONObject();
		paramJson.put("method", "loadQgcjk");
		paramJson.put("key", "");
		paramJson.put("username", "admin");
		JSONObject json = new JSONObject();
		servlet.exportQgcjk(paramJson, json);
	}
	
	public static void testImportDeptPerson(){
		ApiServlet servlet = new ApiServlet();
		JSONObject paramJson = new JSONObject();
		paramJson.put("method", "loadQgcjk");
		paramJson.put("key", "");
		paramJson.put("username", "admin");
		JSONObject json = new JSONObject();
		String filepath = "D:/JavaWorkSpaces/fhy/WebRoot/template/人员信息表-Template.xls";
		try {
			servlet.importDeptPerson(filepath,33);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
