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
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zondy.listener.ApplicationListener;
import com.zondy.util.FileUtils;

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class DownloadServlet extends HttpServlet {

	public DownloadServlet() {
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
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		String fpath = request.getParameter("filepath");
		System.out.println("filepath="+fpath);
		try{
			String rootPath = ApplicationListener.rootPath;
			String filepath = rootPath + fpath;
			File file = new File(filepath);
			if(file.exists()){
				FileUtils.download(response, filepath);
				System.out.println("文件下载成功！"+filepath);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getServletInfo() {
		return "This is my default servlet created by Eclipse";
	}

	public void init() throws ServletException {
		
	}
}
