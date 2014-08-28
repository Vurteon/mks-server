package dao.main;

import beans.main.PhotoDesBean;
import model.uploadpart.StatusRowSetManger;
import utils.db.*;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;


public class CacheRowSetDao {

	/**
	 * 从数据库中查询出时间最近的N条记录，然后将其封装成CacheRowSet
	 */
	public static CachedRowSet buildNewCacheRowSet() throws SQLException {
		Connection con = GetConnection.getMySqlConnection();

		Statement statement = null;
		ResultSet resultSet = null;

		CachedRowSet cachedRowSet;

		// 从StatementFeed、DetailWords、PhotoLocation三个表中依据rs_id查询前1000条数据
		String createCacheRowSet = "SELECT * FROM StatementFeed natural join DetailWords natural join PhotoLocation natural join PhotoPath LIMIT 1000";

		try {
			// 设置ResultSet是可前后滚动的、可更新的
			statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

			resultSet = statement.executeQuery(createCacheRowSet);

			// 构建cacheRowSet
			cachedRowSet = GetRowSetFactory.getRowSetFactory().createCachedRowSet();
			cachedRowSet.populate(resultSet);

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			if(resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		}
		return cachedRowSet;
	}


	/**
	 * 向数据库插入PhotoDesBean的相关数据
	 * @param photoDesBean 新上传的照片的相关数据
	 * @return 成功插入CachedRowSet返回true，否则返回false
	 * @throws SQLException
	 */
	public static boolean insertData(PhotoDesBean photoDesBean) throws SQLException {

		CachedRowSet cachedRowSet = StatusRowSetManger.getStatementRowSet();

		cachedRowSet.last();
		cachedRowSet.moveToInsertRow();

		// 更新数据
		cachedRowSet.updateInt(1,photoDesBean.getRs_id());
		cachedRowSet.updateInt(2,photoDesBean.getID());
		cachedRowSet.updateTimestamp(3, photoDesBean.getTime());
		cachedRowSet.updateString(4, photoDesBean.getPhotoClass());
		cachedRowSet.updateString(5, photoDesBean.getPhotoAt());
		cachedRowSet.updateString(6,photoDesBean.getPhotoTopic());
		cachedRowSet.updateString(10,photoDesBean.getOlderWords());
		cachedRowSet.updateString(11,photoDesBean.getMyWords());
		cachedRowSet.updateString(12,photoDesBean.getPhotoLocation());
		cachedRowSet.updateString(13,photoDesBean.getViewPhotoPath());
		cachedRowSet.updateString(14,photoDesBean.getDetailPhotoPath());

		// 插入数据
		cachedRowSet.insertRow();
		cachedRowSet.moveToCurrentRow();

		cachedRowSet.last();

//		do {
//			System.out.print("rs_id:" + cachedRowSet.getInt(1) + " ");
//			System.out.print("id:" + cachedRowSet.getInt(2) + " ");
//			System.out.print("time:" + cachedRowSet.getTime(3) + " ");
//			for (int i = 4; i < 15; i++) {
//				System.out.print(cachedRowSet.getMetaData().getColumnName(i) + ":" + cachedRowSet.getString(i) + " ");
//			}
//			System.out.println();
//		}while (cachedRowSet.previous());
		return true;
	}


	public static void deleteData() {

	}

	public static void updateData() {

	}

	public static void selectData() {

	}

}
