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

package com.zondy.listener;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zondy.config.XmlConfig;
import com.zondy.database.dao.BaseDAOImpl;

/**
 * 模块名称：ApplicationListener									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2013-9-17 上午09:44:15					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2013-9-17 上午09:44:15					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class ApplicationListener implements ServletContextListener {
	
	//在线用户信息
	public static HashMap<String, String> userOnLine = new LinkedHashMap<String, String>();
	//系统发布目录
	public static String rootPath = "D:/JavaWorkSpaces/fhy/WebRoot/";//84.160服务器
	//webconfig配置文件路径
	public static String webconfigFilePath = rootPath+"WEB-INF/resources/xml/webconfig.xml";
	//sqlconfig配置文件路径
	public static String sqlconfigFilePath = rootPath+"WEB-INF/resources/xml/SQLConfig.xml";
	//菜单页面数据
	public static List<?> pagedata = null;
	// web请求上下文
	private static ServletContext context;

	// 应用上下文
	public static ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
	public static BaseDAOImpl dao = (BaseDAOImpl)ctx.getBean("dao");

	/**
	 * 系统停止
	 */
	public void contextDestroyed(ServletContextEvent event) {
		String webName = event.getServletContext().getContextPath();
		webName = webName.substring(1, webName.length());
	}
	
	/**
	 * 系统启动初始化
	 */
	public void contextInitialized(ServletContextEvent event) {
		String webName = event.getServletContext().getContextPath();
		webName = webName.substring(1, webName.length());
		context = event.getServletContext();
		rootPath = context.getRealPath("/");
		rootPath = rootPath.replace("\\", "/");
		System.out.println("WEB应用系统根目录rootPath:" + rootPath);
		webconfigFilePath = rootPath + "WEB-INF/resources/xml/webconfig.xml";
		sqlconfigFilePath = rootPath + "WEB-INF/resources/xml/SQLConfig.xml";
		System.out.println("SQL语句配置文件路径："+sqlconfigFilePath);
		System.out.println("WEB配置文件路径："+webconfigFilePath);
		//缓存页面数据
		pagedata = loadPageData();
		System.out.println(pagedata);
	}
	
	public static List<?> loadPageData(){
		XmlConfig xml = new XmlConfig(sqlconfigFilePath);
		String sql = xml.getConfigValue("getRoleRight");
		return dao.listAll(sql);
	}
}

