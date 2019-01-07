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

package com.zondy.hibernate.dao;
/**
 * 模块名称：BaseDAOImpl									<br>
 * 功能描述：该文件详细功能描述							<br>
 * 文档作者：雷志强									<br>
 * 创建时间：2014-2-27 下午02:36:18					<br>
 * 初始版本：V1.0	最初版本号							<br>
 * 修改记录：											<br>
 * *************************************************<br>
 * 修改人：雷志强										<br>
 * 修改时间：2014-2-27 下午02:36:18					<br>
 * 修改内容：											<br>
 * *************************************************<br>
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.zondy.listener.ApplicationListener;

/** 统一数据访问接口实现 */
@SuppressWarnings("deprecation")
public class HibernateDAOImpl implements HibernateDAO {
	
	private static Logger log = Logger.getLogger(HibernateDAOImpl.class);
	//通过@Resource注解注入HibernateTemplate实例

	private HibernateTemplate hibernateTemplate = (HibernateTemplate)ApplicationListener.ctx.getBean("hibernateTemplate");

	@SuppressWarnings({ "rawtypes" })
	@Override
	public synchronized Object loadById(Class clazz, Serializable id) {
		Object obj = null;
		Session session = hibernateTemplate.getSessionFactory().openSession();
		session.beginTransaction();
		try {
			obj= session.load(clazz, id);
			if(obj!=null){
				log.info("loadById[obj]="+obj);
			}
		} catch (Exception e) {
			log.error("loadById[Exception]",new Throwable(e));
		}
		session.getTransaction().commit();
		session.close();
		return obj;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public synchronized Object loadObject(final String hql) {
		return hibernateTemplate.execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException{
				Query query = session.createQuery(hql);
				return query.uniqueResult();
			}
		});
	}

	/**
	 * 加锁，支持并发
	 */
	@Override
	public synchronized int saveObject(Object obj) {
		int ret = 0;
		Session session = hibernateTemplate.getSessionFactory().openSession();
		session.beginTransaction();
		session.saveOrUpdate(obj);
		session.getTransaction().commit();
		session.close();
		ret = 1;
		return ret;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public synchronized List listAll(String hql) {
		List list = new ArrayList();
		Session session = hibernateTemplate.getSessionFactory().openSession();
		session.beginTransaction();
		list = session.createQuery(hql).list();
		session.getTransaction().commit();
		session.close();
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public synchronized List listAll(String hql,int pageNo,int pageSize) {
		List<Object> list = new ArrayList();
		Session session = hibernateTemplate.getSessionFactory().openSession();
		session.beginTransaction();
		Query query = session.createQuery(hql);
		query.setMaxResults(pageSize);
		query.setFirstResult((pageNo-1)*pageSize);
		list = query.list();
		session.getTransaction().commit();
		session.close();
		return list;
	}

	@Override
	public synchronized int bulkDelete(String clazz, Object[] ids) {
		int ret = 0;
		Session session = hibernateTemplate.getSessionFactory().openSession();
		session.beginTransaction();
		for (Object object : ids) {
			session.delete(object);
		}
		session.getTransaction().commit();
		session.close();
		ret = 1;
		return ret;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public synchronized int delById(Class clazz, Serializable id) {
		int ret = 0;
		Session session = hibernateTemplate.getSessionFactory().openSession();
		session.beginTransaction();
		try {
			Object obj= session.load(clazz, id);
			session.delete(obj);
		} catch (Exception e) {
			log.error("loadById[Exception]",new Throwable(e));
		}
		session.getTransaction().commit();
		session.close();
		ret = 1;
		return ret;
	}

	@Override
	public synchronized int delBySql(final String hql) {
		int ret = 0;
		Session session = hibernateTemplate.getSessionFactory().openSession();
		session.beginTransaction();
		try {
			ret = session.createQuery(hql).executeUpdate();
		} catch (Exception e) {
			log.error("loadById[Exception]",new Throwable(e));
		}
		session.getTransaction().commit();
		session.close();
		return ret;
	}

	@Override
	public synchronized int bulkSaveOrUpdate(List<Object> list) {
		int ret = 0;
		if(list!=null){
			Session session = hibernateTemplate.getSessionFactory().openSession();
			session.beginTransaction();
			int count = list.size();
			Object vo = null;
			int okCount = 0;
			for(int i=0;i<count;i++){
				vo = list.get(i);
				session.saveOrUpdate(vo);
				okCount++;
			}
			session.getTransaction().commit();
			session.close();
			ret = okCount;
		}
		return ret;
	}
}
