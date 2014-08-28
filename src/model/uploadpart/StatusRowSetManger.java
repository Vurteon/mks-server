package model.uploadpart;

import beans.main.PhotoDesBean;
import dao.main.CacheRowSetDao;
import utils.db.TimeBuilder;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

/**
 * author: 康乐
 * time: 2014/8/22
 * function: 提供对RowSet的相关操作(增、删、改、查)、将RowSetDao所需要的信息进行封装
 */

public class StatusRowSetManger {

	public static CachedRowSet statementRowSet;

	public static int rs_id;

	private static final Object object = new Object();

	public static boolean insertStatus(PhotoDesBean photoDesBean) throws SQLException {

		photoDesBean.setTime(TimeBuilder.getLocalTime());

		photoDesBean.setRs_id(getRs_id());

		synchronized (object) {
			return CacheRowSetDao.insertData(photoDesBean);
		}
	}

	public static void updateStatus() {

	}

	public static void selectStatus() {

	}

	public static void deleteStatus() {

	}


	public static CachedRowSet getStatementRowSet() {
		return statementRowSet;
	}

	public static void setStatementRowSet(CachedRowSet statementRowSet) {
		StatusRowSetManger.statementRowSet = statementRowSet;
	}


	public static int getRs_id() {
		// 返回rs_id后自增1
		return rs_id++;
	}

}
