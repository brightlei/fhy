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
package com.zondy.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zondy.config.XmlConfig;
import com.zondy.hibernate.bean.FhyVo;
import com.zondy.hibernate.dao.HibernateDAOImpl;
import com.zondy.listener.ApplicationListener;
import com.zondy.util.DateUtils;
import com.zondy.util.ExcelUtils;
import com.zondy.util.RandomUtils;

/**
 * 防护员信息工具
 * 
 * @author 雷志强
 * @version 1.0
 */
public class FhyScoreImpl {

	private static Logger log = Logger.getLogger(FhyScoreImpl.class);

	public static int importData(String filepath) throws FileNotFoundException,
			IOException {
		int successCount = 0;
		File file = new File(filepath);
		if (file.exists()) {
			Workbook workbook = ExcelUtils.getWorkbook(filepath);
			Sheet sheet = workbook.getSheetAt(0);
			ExcelUtils utils = new ExcelUtils(workbook, sheet);
			List<List<String>> dataList = utils.getSheetData(sheet);
			if (dataList != null) {
				int count = dataList.size();
				log.info("表格数据总行数：" + count);
				XmlConfig xml = new XmlConfig(
						ApplicationListener.webconfigFilePath);
				String fields = xml.getConfigValue("fhyExcelField");
				String[] fieldArr = fields.split("[|]");
				int fieldCount = fieldArr.length;
				List<String> rowList = null;
				JSONObject fhyJson = null;
				JSONObject voJson = null;
				HibernateDAOImpl dao = new HibernateDAOImpl();
				FhyVo vo = null;
				long st = System.currentTimeMillis();
				List<Object> fhyList = new ArrayList<Object>();
				for (int i = 2; i < count; i++) {
					fhyJson = new JSONObject();
					rowList = dataList.get(i);
					// 一行的列数
					int rowSize = rowList.size();
					log.info("rowList=" + rowList);
					log.info("[rowSize-fieldCount]=" + rowSize + "|"
							+ fieldCount);
					/**
					 * if (rowList.size() == fieldCount) { for (int k = 0; k <
					 * fieldCount; k++) { fhyJson.put(fieldArr[k],
					 * rowList.get(k)); } }
					 */
					for (int k = 0; k < fieldCount; k++) {
						if (k == rowSize) {
							rowList.add("");
						}
						fhyJson.put(fieldArr[k], rowList.get(k));
					}
					fhyJson.put("state", 1);
					fhyJson.put("headimg", "");
					vo = (FhyVo) dao.loadObject("from FhyVo where bh='"
							+ fhyJson.getString("bh") + "'");
					if (vo == null) {
						vo = JSON.toJavaObject(fhyJson, FhyVo.class);
					} else {
						voJson = JSONObject.parseObject(JSONObject
								.toJSONString(vo));
						voJson.putAll(fhyJson);
						voJson.put("xgsj",
								DateUtils.date2String("yyyy-MM-dd HH:mm:ss"));
						vo = JSON.toJavaObject(voJson, FhyVo.class);
					}
					fhyList.add(vo);
				}
				successCount = dao.bulkSaveOrUpdate(fhyList);
				long et = System.currentTimeMillis();

				// XmlConfig xml = new
				// XmlConfig(ApplicationListener.webconfigFilePath);
				// XmlConfig sqlxml = new
				// XmlConfig(ApplicationListener.sqlconfigFilePath);
				// String fields = xml.getConfigValue("fhyExcelField");
				// String[] fieldArr = fields.split("[|]");
				// JSONObject paramJson = null;
				// String sql = "";
				// List<String> rowList = null;
				// for(int i=1;i<count;i++){
				// paramJson = new JSONObject();
				// paramJson.put("primaryKey", RandomUtils.getUid());
				// rowList = dataList.get(i);
				// for(int k=0;k<fieldArr.length;k++){
				// paramJson.put(fieldArr[k], rowList.get(k));
				// }
				// sql = sqlxml.getParamConfig("insertFhy", paramJson);
				// successCount += ApplicationListener.dao.saveObject(sql);
				// }
				log.info("成功导入[" + successCount + "]条数据！共耗时：(" + (et - st)
						+ "ms)");
			}
		} else {
			log.warn("文件不存在！" + filepath);
		}
		return successCount;
	}

	public static JSONObject getFhy(JSONObject paramJson) {
		JSONObject json = new JSONObject();
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String datasql = sqlXml.getParamConfig("getfhy", paramJson);
		String countsql = sqlXml.getParamConfig("getfhycount", paramJson);
		int page = 1;
		int rows = 100;
		if (paramJson.containsKey("page")) {
			page = paramJson.getIntValue("page");
		}
		if (paramJson.containsKey("rows")) {
			rows = paramJson.getIntValue("rows");
		}
		int total = ApplicationListener.dao.countAll(countsql);
		List<?> data = ApplicationListener.dao.listAll(datasql, page, rows);
		json.put("total", total);
		json.put("rows", data);
		return json;
	}

	/**
	 * @Description 加载所有人员信息.<br>
	 * @author 雷志强 2018年12月29日 下午10:53:49
	 * @return List<?>
	 */
	public static List<?> loadAllPerson() {
		XmlConfig sqlXml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
		String sql = sqlXml.getConfigValue("getAllPerson");
		List<?> list = ApplicationListener.dao.listAll(sql);
		return list;
	}

	/**
	 * @Description 导入车间日计划作为全过程监控数据.<br>
	 * @author 雷志强 2018年12月15日 下午8:47:28
	 * @param filepath
	 *            文件路径
	 * @param username
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return int
	 */
	public static int importQgcjkData(String filepath, String username)
			throws FileNotFoundException, IOException {
		int successCount = 0;
		File file = new File(filepath);
		if (file.exists()) {
			Workbook workbook = ExcelUtils.getWorkbook(filepath);
			Sheet sheet = workbook.getSheetAt(0);
			ExcelUtils utils = new ExcelUtils(workbook, sheet);
			List<List<String>> dataList = utils.getSheetData(sheet);
			if (dataList != null) {
				int count = dataList.size();
				log.info("表格数据总行数：" + count);
				List<String> rowList = dataList.get(1);
				log.info("表格头部字段：" + rowList);
				XmlConfig webxml = new XmlConfig(ApplicationListener.webconfigFilePath);
				XmlConfig sqlxml = new XmlConfig(ApplicationListener.sqlconfigFilePath);
				String fields = webxml.getConfigValue("qgcjkImportExcelField");
				String[] fieldArr = fields.split("[|]");
				int fieldCount = fieldArr.length;
				log.info("配置的字段为：" + fields);
				log.info("字段数=" + fieldCount);
				JSONObject sqlParam = null;
				String sql = null;
				String sd = null;
				String[] sdArr = null;
				long st = System.currentTimeMillis();
				List<String> sqlList = new ArrayList<String>();
				// 获取所有人员信息
				List<?> personList = loadAllPerson();
				List<String> noParamList = new ArrayList<String>();
				// 头部就两行，从2开始数据导入
				for (int i = 2; i < count; i++) {
					rowList = dataList.get(i);
					if (!checkRowIsNull(rowList)) {
						if (rowList.size() == fieldCount) {
							sqlParam = new JSONObject();
							sqlParam.put("opuser", username);
							for (int k = 0; k < fieldCount; k++) {
								sqlParam.put(fieldArr[k], rowList.get(k));
							}
							sd = sqlParam.getString("zysd");
							if (sd != null && !sd.equals("")) {
								if (sd.length() > 11) {
									sd = sd.substring(0, 11);
									sdArr = sd.split("[-]");
									sqlParam.put("zzdgsj", sdArr[0]);
									sqlParam.put("zzlgsj", sdArr[1]);
									sqlParam.put("sjrwsj", sdArr[0]);
									sqlParam.put("sjcwsj", sdArr[1]);
									sqlParam.put("tckssj", sdArr[0]);
									sqlParam.put("tcsjsj", sdArr[1]);
								}
							}
							sqlParam.put("qgcjkbh", RandomUtils.genItemId());
							sqlParam.put("zz",getPersonBhString(personList,sqlParam.getString("zz")));
							sqlParam.put("xc",getPersonBhString(personList,sqlParam.getString("xc")));
							sqlParam.put("yd",getPersonBhString(personList,sqlParam.getString("yd")));
							sqlParam.put("fxyd",getPersonBhString(personList,sqlParam.getString("fxyd")));
							// 20190110-将“计划作业地点”、“作业项目”项填入后面的“实际作业地点”、“完成工作量”中。这样人员在上报中可少量编辑即可完成
							sqlParam.put("sjzydd", sqlParam.getString("bgdd"));
							sqlParam.put("wcgzl", sqlParam.getString("zyxm"));
							sql = sqlxml.getParamConfig("importQgcjk", sqlParam);
							noParamList = getSqlParam(sql);
							for (int x = 0; x < noParamList.size(); x++) {
								sqlParam.put(noParamList.get(x), "");
							}
							sql = sqlxml.getParamConfig("importQgcjk", sqlParam);
							sqlList.add(sql);
						}
					}
				}
				successCount = ApplicationListener.dao.betchSaveData(sqlList);
				long et = System.currentTimeMillis();
				log.info("成功导入[" + successCount + "]条数据！共耗时：(" + (et - st) + "ms)");
			}
		} else {
			log.warn("文件不存在！" + filepath);
		}
		return successCount;
	}

	/**
	 * @Description 根据人员姓名获取人员编号信息.<br>
	 * @author 雷志强 2018年12月29日 下午10:55:26
	 * @param personList
	 *            所有人员
	 * @param persons
	 *            人员姓名
	 * @return String
	 */
	private static String getPersonBhString(List<?> personList, String persons) {
		String bhstring = persons;
		List<String> bhList = new ArrayList<String>();
		if (personList != null) {
			int count = personList.size();
			if (persons == null || persons.equals("")) {
				return bhstring;
			}
			String[] personArr = persons.split("[,]");
			List<String> pList = Arrays.asList(personArr);
			JSONObject record = null;
			String xm = null;
			for (int i = 0; i < count; i++) {
				record = (JSONObject) personList.get(i);
				xm = record.getString("xm");
				if (bhList.size() == pList.size()) {
					break;
				}
				if (pList.contains(xm)) {
					bhList.add(record.getString("bh"));
				}
			}
		}
		if (bhList.size() > 0) {
			bhstring = StringUtils.join(bhList.toArray(), ",");
		}
		return bhstring;
	}

	/**
	 * @Description 检查数据表中一行记录是否全部为空数据.<br>
	 * @author 雷志强 2018年12月15日 上午12:00:55
	 * @param rowList
	 * @return boolean true-全部为空|false-有数据
	 */
	private static boolean checkRowIsNull(List<String> rowList) {
		boolean isnull = true;
		if (rowList != null) {
			int count = rowList.size();
			String value = null;
			for (int i = 0; i < count; i++) {
				value = rowList.get(i);
				if (value != null && !value.equals("")) {
					isnull = false;
					break;
				}
			}
		}
		return isnull;
	}

	/**
	 * 检查SQL语句中是否有未替换的参数.<br>
	 * 
	 * @param sql
	 *            SQL语句
	 * @return List<String> 参数集合
	 */
	public static List<String> getSqlParam(String sql) {
		int length = 0;
		if (sql != null) {
			length = sql.length();
		}
		List<String> list = new ArrayList<String>();
		List<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < length; i++) {
			if (sql.charAt(i) == '§') {
				indexList.add(i);
			}
		}
		String param = "";
		int firstIndex = 0;
		int nextIndex = 0;
		for (int i = 0; i < indexList.size() - 1; i = i + 2) {
			firstIndex = indexList.get(i);
			nextIndex = indexList.get(i + 1);
			param = sql.substring(firstIndex + 1, nextIndex);
			list.add(param);
		}
		return list;
	}

	public static int importPjzbData(String filepath, String zbid)
			throws FileNotFoundException, IOException {
		int successCount = 0;
		File file = new File(filepath);
		if (file.exists()) {
			Workbook workbook = ExcelUtils.getWorkbook(filepath);
			Sheet sheet = workbook.getSheetAt(0);
			ExcelUtils utils = new ExcelUtils(workbook, sheet);
			List<List<String>> dataList = utils.getSheetData(sheet);
			if (dataList != null) {
				int count = dataList.size();
				log.info("表格数据总行数：" + count);
				List<String> rowList = null;
				XmlConfig webxml = new XmlConfig(
						ApplicationListener.webconfigFilePath);
				XmlConfig sqlxml = new XmlConfig(
						ApplicationListener.sqlconfigFilePath);
				String fields = webxml.getConfigValue("pjzbImportExcelField");
				String[] fieldArr = fields.split("[|]");
				int fieldCount = fieldArr.length;
				System.out.println("字段数：" + fieldCount);
				JSONObject sqlParam = null;
				String sql = null;
				long st = System.currentTimeMillis();
				List<String> sqlList = new ArrayList<String>();
				for (int i = 2; i < count; i++) {
					rowList = dataList.get(i);
					if (!checkRowIsNull(rowList)) {
						// System.out.println(rowList);
						if (rowList.size() == fieldCount) {
							sqlParam = new JSONObject();
							sqlParam.put("zbid", zbid);
							for (int k = 0; k < fieldCount; k++) {
								sqlParam.put(fieldArr[k], rowList.get(k));
							}
							sql = sqlxml
									.getParamConfig("addPjzbData", sqlParam);
							sqlList.add(sql);
						}
					}
				}
				successCount = ApplicationListener.dao.betchSaveData(sqlList);
				long et = System.currentTimeMillis();
				log.info("成功导入[" + successCount + "]条数据！共耗时：(" + (et - st)
						+ "ms)");
			}
		} else {
			log.warn("文件不存在！" + filepath);
		}
		return successCount;
	}

	public static void main(String[] args) {
		// FhyScoreImpl impl = new FhyScoreImpl();
		String filepath = "D:/JavaWorkSpaces/fhy/WebRoot/template/防护员信息表-Template.xls";
		filepath = "C:/Users/Administrator/Downloads/防护员信息表数据导入模板 (2).xls";
		filepath = "H:/个人资料/研发产品/04.工务段防护员管理系统/云梦线路车间_2018-12-12_日计划.xls";
		filepath = "H:/个人资料/研发产品/04.工务段防护员管理系统/襄北线路车间2019-02-13日计划.xls";
		//filepath = "H:/个人资料/研发产品/04.工务段防护员管理系统/襄北线路车间2019-02-18至2019-02-18日计划.xls";
		// filepath =
		// "D:/JavaWorkSpaces/fhy/WebRoot/template/评价指标数据表-Template.xls";
		try {
			// filepath =
			// "D:/JavaWorkSpaces/fhy/WebRoot/template/防护员信息表-Template.xls";
			// filepath = "H:/个人资料/研发产品/04.工务段防护员管理系统/客户提供数据/防护员信息表数据导入模板.xls";
			// importData(filepath);
			// impl.importPjzbData(filepath,"2");
			FhyScoreImpl.importQgcjkData(filepath, "admin");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test() throws Exception {
		String filepath = "c:/1111.xls";
		Workbook book = ExcelUtils.getWorkbook(filepath);
		Sheet sheet = book.getSheetAt(0);
		ExcelUtils utils = new ExcelUtils(book, sheet);
		String fields = "bh:编号|xm:姓名|cj:车间|gq:工区|xb:性别|age:年龄|sjh:手机号|zzmm:政治面貌|dqfhzg:当前防护资格|cjpxsj:参数培训时间";
		JSONArray datalist = new JSONArray();
		// List<List<String>> list = new ArrayList<List<String>>();
		// List<String> rowList = new ArrayList<String>();
		int index = 10000;
		String[] gqArr = new String[] { "客整", "襄阳线路", "西湾", "三场线路", "小清河",
				"襄阳东", "三场维修" };
		String[] zzmmArr = new String[] { "党员", "群众", "共青团员" };
		JSONObject record = null;
		for (int i = 0; i < 2000; i++) {
			record = new JSONObject();
			record.put("bh", "" + (index + i + 1));
			record.put("xm", RandomUtils.getChineseName());
			record.put("cj", "襄阳线路车间");
			record.put("gq", gqArr[RandomUtils.getInt(0, gqArr.length - 1)]);
			record.put("xb", "男");
			record.put("age", "" + RandomUtils.getInt(40, 60));
			record.put("sjh", RandomUtils.getTel());
			record.put("zzmm",
					zzmmArr[RandomUtils.getInt(0, zzmmArr.length - 1)]);
			record.put("dqfhzg", "驻站");
			record.put(
					"cjpxsj",
					DateUtils.date2String(
							"yyyy-MM-dd",
							DateUtils.calculateByDate(new Date(), -1
									* RandomUtils.getInt(100, 300))));
			datalist.add(record);
		}
		utils.export(filepath, "防护人员", fields, datalist);
	}
}
