package model.uploadpart;

import beans.main.PhotoDesBean;
import dao.main.CacheRowSetDao;

import javax.sql.rowset.CachedRowSet;

/**
 * author: 康乐
 * time: 2014/8/22
 * function: 提供对RowSet的相关操作(增、删、改、查)、将RowSetDao所需要的信息进行封装
 */

public class StatusRowSetManger {

	public static CachedRowSet statementRowSet;

	public static void insertStatus(PhotoDesBean photoDesBean) {








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
}
