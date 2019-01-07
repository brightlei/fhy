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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.ss.util.CellRangeAddressList;
import java.io.IOException;
import java.io.FileOutputStream;
/**
 * 该文件详细功能描述
 * @author 雷志强
 * @version 1.0
 */
public class PoiTest1 {
	public static void main(String [] args) throws IOException {
        HSSFWorkbook wb=new HSSFWorkbook();//excel文件对象
        HSSFSheet sheetlist=wb.createSheet("sheetlist");//工作表对象
        FileOutputStream out=new FileOutputStream("C:/success.xls");
        HSSFDataValidation sexValidate = PoiTest1.setDataValidationList((short)0,(short)600,(short)0,(short)0,"男,女","aaaaaaaa");//PoiTest1.setBoxs();
        sheetlist.addValidationData(sexValidate);
        wb.write(out);
        out.close();
        System.out.println("OK");
    }
    
    public static HSSFDataValidation setDataValidationList(short firstRow,short endRow,short firstCol,short endCol,String text,String tips){
    	//设置下拉列表的内容
    	String[] textlist = text.split("[,]");
    	//加载下拉列表内容
    	DVConstraint constraint=DVConstraint.createExplicitListConstraint(textlist);
    	//设置数据有效性加载在哪个单元格上。
		//四个参数分别是：起始行、终止行、起始列、终止列
    	CellRangeAddressList regions=new CellRangeAddressList(firstRow,endRow,firstCol,endCol);
    	//数据有效性对象
    	HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
    	dataValidation.setSuppressDropDownArrow(false);
 		dataValidation.createPromptBox("输入提示", tips);
 		dataValidation.setShowPromptBox(true);
    	return dataValidation;
    }
    
    public static HSSFDataValidation setDataValidationView(short firstRow,short firstCol,short endRow, short endCol){
    	//构造constraint对象
    	DVConstraint constraint=DVConstraint.createCustomFormulaConstraint("B1");
    	//四个参数分别是：起始行、终止行、起始列、终止列
    	CellRangeAddressList regions=new CellRangeAddressList(firstRow,firstCol,endRow,endCol);
    	//数据有效性对象
    	HSSFDataValidation data_validation_view = new HSSFDataValidation(regions, constraint);
    	
    	return data_validation_view;
    }
    
 // 下拉框限制
 	public static HSSFDataValidation setBoxs() {
 		CellRangeAddressList addressList = new CellRangeAddressList(0, 600, 0, 0);
 		final String[] DATA_LIST = new String[] { "男", "女", };
 		DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(DATA_LIST);
  
 		HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
 		dataValidation.setSuppressDropDownArrow(false);
 		dataValidation.createPromptBox("输入提示", "请从下拉列表中选择男女");
 		dataValidation.setShowPromptBox(true);
  
 		return dataValidation;
 	}
 	
}
