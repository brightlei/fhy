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

package com.zondy.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * 模块名称：HttpRequest									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：Dec 21, 2016 2:32:28 PM					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：Dec 21, 2016 2:32:28 PM					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class HttpRequest {
	private static Logger log = Logger.getLogger(HttpRequest.class);

	/**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (final String key : map.keySet()) {
//               System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error("发送GET请求出现异常！",new Throwable(e));
        	//System.out.println("发送GET请求出现异常！" + e);
            //e.printStackTrace();
        }
        // 使用finally块来关闭输入流 200
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
            	log.error("Exception异常！",new Throwable(ex));
            }
        }
        return result;
    }
    
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                //System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error("发送GET请求出现异常！",new Throwable(e));
        	//System.out.println("发送GET请求出现异常！" + e);
            //e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
            	log.error("Exception异常！",new Throwable(ex));
            }
        }
        return result;
    }
    /**
     * 给请求设置请求头参数.<br>
     * @param connection 连接对象
     * @param headMap 请求头map对象，例如：{"Authorization":"Basic VUUwMDAwMDAwMDA6VUUwMDAwMDAwMDA="}
     * @return void
     */
    private static void setRequestHeader(URLConnection connection,Map<String, String> headMap){
    	//设置请求头
        Set<Entry<String, String>> set = headMap.entrySet();
        Iterator<Entry<String, String>> iterator = set.iterator();
        Entry<String, String> entry = null;
        String key = null;
        String val = null;
        while(iterator.hasNext()){
        	entry = iterator.next();
        	key = entry.getKey();
        	val = entry.getValue();
        	if(val == null){
        		val = "";
        	}
        	connection.setRequestProperty(key, val);
        }
    }
    
    /**
     * 向指定URL发送GET方法的请求.<br>
     * @param url 请求地址，例如：http://192.168.10.158:8080/test/json/service?a=123
     * @param headMap 请求头map对象，例如：{"Authorization":"Basic VUUwMDAwMDAwMDA6VUUwMDAwMDAwMDA="}
     * @return String 请求响应结果字符串
     */
    public static String sendGet(String url,Map<String, String> headMap) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //设置请求头
            setRequestHeader(connection,headMap);
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error("发送GET请求出现异常！",new Throwable(e));
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
            	log.error("Exception异常！",new Throwable(ex));
            }
        }
        return result;
    }
 
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            //in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	log.error("发送POST请求出现异常！",new Throwable(e));
            //System.out.println("发送 POST 请求出现异常！"+e);
            //e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	log.error("IOException异常！",new Throwable(ex));
                //ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 向指定 URL 发送POST方法的请求.<br>
     * @param url 发送请求的 URL
     * @param param 请求参数字符串
     * @param headMap 请求头map对象，例如：{"Authorization":"Basic VUUwMDAwMDAwMDA6VUUwMDAwMDAwMDA="}
     * @return String 请求响应结果字符串
     */
    public static String sendPost(String url, String param,Map<String, String> headMap) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //设置请求头
            setRequestHeader(conn,headMap);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	log.error("发送POST请求出现异常！",new Throwable(e));
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	log.error("IOException异常！",new Throwable(ex));
            }
        }
        return result;
    }
    
	public static String uploadFileImpl(String url, String filepath){
		OutputStream out = null;
        BufferedReader reader = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
            //设置POST请求
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //设置请求头参数
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            // 获取URLConnection对象对应的输出流
            out = new DataOutputStream(conn.getOutputStream());
            //上传文件
            // 数据输入流,用于读取文件数据
            File file = new File(filepath);
            InputStream fis = new FileInputStream(file);
            DataInputStream dis = new DataInputStream(fis);
            byte[] bufferOut = new byte[1024*8];
            int bytes = 0;
            // 每次读8KB数据,并且将文件数据写入到输出流中
            while ((bytes = dis.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            dis.close();
            // flush输出流的缓冲
            out.flush();
            out.close();
            // 定义BufferedReader输入流来读取URL的响应
            //in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	log.error("发送POST请求出现异常！",new Throwable(e));
            //System.out.println("发送 POST 请求出现异常！"+e);
            //e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(reader!=null){
                	reader.close();
                }
            }
            catch(IOException ex){
            	log.error("IOException异常！",new Throwable(ex));
                //ex.printStackTrace();
            }
        }
        return result;
	}
	
    public static String sendPut(String url,String param){
    	String result = "";
    	PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            httpURLConnection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            httpURLConnection.setRequestMethod("PUT");
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	log.error("发送PUT请求出现异常！",new Throwable(e));
            //System.out.println("发送 POST 请求出现异常！"+e);
            //e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	log.error("IOException异常！",new Throwable(ex));
            }
        }
    	
    	return result;
    }

    /**
     * 向指定的URL发送PUT请求.<br>
     * @param url 发送请求的 URL
     * @param param 请求参数字符串
     * @param headMap 请求头map对象，例如：{"Authorization":"Basic VUUwMDAwMDAwMDA6VUUwMDAwMDAwMDA="}
     * @return String 请求响应结果字符串
     */
    public static String sendPut(String url,String param,Map<String, String> headMap){
    	String result = "";
    	PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            httpURLConnection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //设置请求头
            setRequestHeader(httpURLConnection,headMap);
            //设置请求方式
            httpURLConnection.setRequestMethod("PUT");
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	log.error("发送PUT请求出现异常！",new Throwable(e));
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	log.error("IOException异常！",new Throwable(ex));
            }
        }
    	return result;
    }
    
    //发送DELETE请求
    public static String sendDelete(String url){
    	String result = "";
        BufferedReader in = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            httpURLConnection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            httpURLConnection.setRequestMethod("DELETE");
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	log.error("发送DELETE请求出现异常！",new Throwable(e));
        }
        //使用finally块来关闭输出流、输入流
        finally{
        	try{
                if(in!=null){
                    in.close();
                }
                if(httpURLConnection!=null){
                	httpURLConnection.disconnect();
                }
            }
            catch(IOException ex){
            	log.error("IOException异常！",new Throwable(ex));
            }
        }
    	return result;
    }
    
    public static String sendDelete(String url,Map<String, String> headMap){
    	String result = "";
        BufferedReader in = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            httpURLConnection = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //设置请求头
            setRequestHeader(httpURLConnection,headMap);
            //设置请求方式
            httpURLConnection.setRequestMethod("DELETE");
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	log.error("发送DELETE请求出现异常！",new Throwable(e));
        }
        //使用finally块来关闭输出流、输入流
        finally{
        	try{
                if(in!=null){
                    in.close();
                }
                if(httpURLConnection!=null){
                	httpURLConnection.disconnect();
                }
            }
            catch(IOException ex){
            	log.error("IOException异常！",new Throwable(ex));
            }
        }
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public static JSONObject paramToJson(Map<String, Object> pMap){
		JSONObject paraJson = new JSONObject();
		Set<?> set = pMap.entrySet();
		Iterator<?> iterator =  set.iterator();
		Entry<String, Object> entry = null;
		while(iterator.hasNext()){
			entry = (Entry<String, Object>)iterator.next();
			String key = entry.getKey().toString();
			String[] value = (String[])entry.getValue();
			paraJson.put(key, value[0]);
		}
		return paraJson;
	}
    
    @SuppressWarnings("unchecked")
	public static JSONObject paramToJson(HashMap<String, String> pMap){
		JSONObject paraJson = new JSONObject();
		Set<?> set = pMap.entrySet();
		Iterator<?> iterator =  set.iterator();
		Entry<String, Object> entry = null;
		while(iterator.hasNext()){
			entry = (Entry<String, Object>)iterator.next();
			String key = entry.getKey().toString();
			String value = (String)entry.getValue();
			paraJson.put(key, value);
		}
		return paraJson;
	}
    
    @SuppressWarnings("unchecked")
	public static JSONObject paramMapToJson(Map<String, String[]> pMap){
		JSONObject paraJson = new JSONObject();
		Set<?> set = pMap.entrySet();
		Iterator<?> iterator =  set.iterator();
		Entry<String, Object> entry = null;
		while(iterator.hasNext()){
			entry = (Entry<String, Object>)iterator.next();
			String key = entry.getKey().toString();
			String[] value = (String[])entry.getValue();
			paraJson.put(key, value[0]);
		}
		return paraJson;
	}
    
    public static String getParamString(JSONObject paramJson) throws UnsupportedEncodingException{
		String param = "";
		Set<Entry<String, Object>> sets = paramJson.entrySet();
		Iterator<Entry<String, Object>> iterator = sets.iterator();
		Entry<String, Object> entry = null;
		String key = null;
		String value = null;
		StringBuffer sb = new StringBuffer();
		while(iterator.hasNext()){
			entry = iterator.next();
			key = entry.getKey();
			value = (String)entry.getValue();
			if(value==null){
				value = "";
			}
			sb.append(key+"="+value).append("&");
		}
		param = sb.toString();
		if(param.length()>0){
			param = param.substring(0,param.length()-1);
		}
		return param;	
	}
	/**
	 * 功能描述：请用一句话描述这个方法实现的功能<br>
	 * 创建作者：雷志强<br>
	 * 创建时间：Dec 21, 2016 2:32:28 PM<br>
	 * @param args
	 * @return void
	 */
	public static void main(String[] args) {
		try {
			//testSendPost();
			testDoGet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	public static void testDoGet(){
		String url = "http://52.80.37.111/msc/users";///UE98517
		Map<String, String> headMap = new HashMap<String, String>();
		headMap.put("Authorization", "Basic VUUwMDAwMDAwMDA6VUUwMDAwMDAwMDA=");
		String jsonStr = sendGet(url,headMap);
		System.out.println(jsonStr);
	}
	
	public static void testUploadFile(){
		String filepath = "d:/aaa.png";
		String serviceUrl = "http://52.80.37.111/fastcfd/fastmicroscale/timeseries";
		String jsonString = uploadFileImpl(serviceUrl, filepath);
		System.out.println(jsonString);
	}
	
	public static void testSendGet() throws UnsupportedEncodingException{
		String srcsrsname = "WGS1984_秒";
		String dessrsname = "地理坐标系(西安)_秒"; 
		srcsrsname = URLEncoder.encode(srcsrsname, "UTF-8");
		dessrsname = URLEncoder.encode(dessrsname, "UTF-8");
		System.out.println(srcsrsname);
		System.out.println(dessrsname);
		String url = "http://localhost:8080/freemeso/service/rest/test/t5?sex=1&a=2&c=3&token=cc9d54c6dd8b41cda4bb93f7c296abbc";
		url = "http://54.222.145.248:6163/igs/rest/mrgs/geomservice/transcoordinate?points=113.32,32.41,0;114.43,30.25,0&dx=100&dy=100&dz=100&srcsrsname="+srcsrsname+"&dessrsname="+dessrsname;
		//url = "http://54.222.145.248:6163/igs/rest/mrgs/geomservice/transcoordinate?points=113.32,32.41,0;114.43,30.25,0&dx=100&dy=100&dz=100&srcsrsname=WGS1984_%E7%A7%92&dessrsname=%E5%9C%B0%E7%90%86%E5%9D%90%E6%A0%87%E7%B3%BB(%E8%A5%BF%E5%AE%89)_%E7%A7%92";
		String result = sendGet(url);
		System.out.println("result="+result);
	}
	
	public static void testSendPost(){
		String url = "http://192.168.10.187:8080/goldwind/service/install/saveInstall";
		JSONObject json = new JSONObject();
		json.put("phoneid", "hgfer8345j");
		json.put("phonelabel", "apple");
		json.put("phoneos", "ios");
		json.put("token", "cc9d54c6dd8b41cda4bb93f7c296abbc");
		String param = json.toJSONString();
		System.out.println(param);
		String result = sendPost(url,param);
		System.out.println("result="+result);
	}
	
	public static void testSendPut(){
		String url = "http://192.168.84.158:8195/bds/rest/ds/dataset.json";
		JSONObject json = new JSONObject();
		json.put("key", "9E698781AC764BBB9558E942F87E2ECD");
		json.put("description", "添加数据集");
		json.put("charset", "UTF-8");
		json.put("datasetName", "GDBP://MapGISLocal/武汉市01/ds/武汉市/sfcls/new_point");
		json.put("fldnames", "[\"LayerID\"]");
		json.put("fldtypes", "[\"String\"]");
		String param = json.toJSONString();
		String result = sendPut(url,param);
		System.out.println("result="+result);
	}
	
	public static void testSendDelete(){
		String url = "http://192.168.84.158:8195/bds/rest/ds/dataset.json?key=9E698781AC764BBB9558E942F87E2ECD&datasetName=gdbp://MapGisLocal/IMSWebGISGDB/sfcls/buffer110240&datasetType=Point";
		url="http://192.168.84.158:8195/bds/rest/ds/feature/1.json?gdbpUrl=gdbp://MapGisLocal/IMSWebGISGDB/sfcls/buffer11957&key=9E698781AC764BBB9558E942F87E2ECD";
		String result = sendDelete(url);
		System.out.println("result="+result);
	}
}
