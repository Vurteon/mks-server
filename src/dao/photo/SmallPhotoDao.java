package dao.photo;

import utils.db.CachedRowSetFactory;
import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/14
 * function：缩略图的DAO
 */

public class SmallPhotoDao {


	/**
	 * 从数据库中获取指定ID的指定条数的缩略图的信息，排序为rs_id升序
	 * @param ID 指定的用户端的ID
	 * @param number 需要获取的数量
	 * @return 缓存在CachedRowSet中的数据
	 * @throws SQLException
	 */
	public static CachedRowSet getSmallPhotoPath(int ID, int number) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		CachedRowSet cachedRowSet = null;

		String getSmallPhotoPath = "SELECT rs_id,more_small_photo,album FROM StatusFeeds NATURAL JOIN PhotoPath WHERE ID = ? order by rs_id desc LIMIT ?";
		try {
			ps = connection.prepareStatement(getSmallPhotoPath);
			ps.setInt(1, ID);
			ps.setInt(2,number);
			resultSet = ps.executeQuery();
			// 将信息填入cache
			cachedRowSet = CachedRowSetFactory.getRowSetFactory().createCachedRowSet();
			cachedRowSet.populate(resultSet);
		} catch (SQLException e) {
			ReleaseSource.releaseSource(cachedRowSet);
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet, ps, connection);
		}
		return cachedRowSet;
	}


	/**
	 * 从数据库中获取指定的ID的位于rs_id后的缩略图的信息
	 * @param ID 指定的需要被获取数据的ID
	 * @param rs_id 资源标记，用于比较所获得的资源；如果所获得的资源的id大于当前所传入的id，则放弃获取
	 * @return 固定条数的、固定ID的、资源的id小于传入的参数的rs_id的缩略图信息
	 * @throws SQLException
	 */
	public static CachedRowSet getSmallPhotoPath(int ID, int rs_id, int number) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		CachedRowSet cachedRowSet = null;

		String getSmallPhotoPath = "SELECT rs_id,more_small_photo,album FROM StatusFeeds NATURAL JOIN PhotoPath WHERE ID = ? and rs_id < ? order by rs_id desc LIMIT ? ";
		try {
			ps = connection.prepareStatement(getSmallPhotoPath);
			ps.setInt(1, ID);
			ps.setInt(2,rs_id);
			ps.setInt(3,number);
			resultSet = ps.executeQuery();
			// 将信息填入cache
			cachedRowSet = CachedRowSetFactory.getRowSetFactory().createCachedRowSet();
			cachedRowSet.populate(resultSet);
		} catch (SQLException e) {
			ReleaseSource.releaseSource(cachedRowSet);
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet, ps, connection);
		}
		return cachedRowSet;
	}



}
