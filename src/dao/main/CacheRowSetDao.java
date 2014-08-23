package dao.main;

import utils.db.GetConnection;
import utils.db.GetRowSetFactory;

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

		// 从StatementFeed、DetailWords、PhotoKocation三个表中依据rs_id查询前1000条数据
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



	public static void insertData() {

	}


	public static void deleteData() {

	}

	public static void updateData() {

	}

	public static void selectData() {

	}

}
