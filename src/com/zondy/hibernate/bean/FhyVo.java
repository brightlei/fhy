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

package com.zondy.hibernate.bean;


/**
 * 模块名称：BkVo									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：Apr 11, 2017 3:06:02 PM					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：Apr 11, 2017 3:06:02 PM					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
public class FhyVo {
	private String bh;//防护员编号(工号)
	private String xm;//防护员姓名
	private String cj;//所属车间
	private String gq;//所属工区
	private String xb;//性别
	private int age;//年龄
	private String sjh;//手机号
	private String zzmm;//政治面貌
	private String dqfhzg;//当前防护资格
	private String cjpxsj;//参加培训时间
	private String headimg;//防护员头像图片地址
	private String cjsj;//创建时间
	private String xgsj;//修改时间
	private int state;//记录状态:0-已删除，1-正常
	
	public String getBh() {
		return bh;
	}
	public void setBh(String bh) {
		this.bh = bh;
	}
	public String getXm() {
		return xm;
	}
	public void setXm(String xm) {
		this.xm = xm;
	}
	public String getCj() {
		return cj;
	}
	public void setCj(String cj) {
		this.cj = cj;
	}
	public String getGq() {
		return gq;
	}
	public void setGq(String gq) {
		this.gq = gq;
	}
	public String getXb() {
		return xb;
	}
	public void setXb(String xb) {
		this.xb = xb;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getSjh() {
		return sjh;
	}
	public void setSjh(String sjh) {
		this.sjh = sjh;
	}
	public String getZzmm() {
		return zzmm;
	}
	public void setZzmm(String zzmm) {
		this.zzmm = zzmm;
	}
	public String getDqfhzg() {
		return dqfhzg;
	}
	public void setDqfhzg(String dqfhzg) {
		this.dqfhzg = dqfhzg;
	}
	public String getCjpxsj() {
		return cjpxsj;
	}
	public void setCjpxsj(String cjpxsj) {
		this.cjpxsj = cjpxsj;
	}
	public String getHeadimg() {
		return headimg;
	}
	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}
	public String getCjsj() {
		return cjsj;
	}
	public void setCjsj(String cjsj) {
		this.cjsj = cjsj;
	}
	public String getXgsj() {
		return xgsj;
	}
	public void setXgsj(String xgsj) {
		this.xgsj = xgsj;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
