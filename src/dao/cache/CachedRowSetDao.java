package dao.cache;

import beans.status.PhotoDesBean;
import model.status.StatusRowSetManager;
import utils.db.*;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.ArrayList;

/**
 * author: 康乐
 * time: 2014/8/27-31
 * function: 提供对内存数据库CachedRowSet的增、删、查操作,其中增操作将会在缓存容量达到一定程度
 * 时进行数据库的同步操作;删除操作,由于所有数据只存在一份,要么在缓存中,要么在数据库,所以删除不需要
 * 同步.
 */

public class CachedRowSetDao {

	/**
	 * 从数据库中查询出时间最近的N条记录，然后将其封装成CacheRowSet
	 */
	public static CachedRowSet buildNewCacheRowSet() throws SQLException {
		Connection con = ConnectionFactory.getMySqlConnection();

		Statement statement = null;
		ResultSet resultSet = null;

		CachedRowSet cachedRowSet;
		// 从StatementFeed、DetailWords、PhotoLocation三个表中依据rs_id查询前1000条数据
		String createCacheRowSet = "SELECT * FROM StatusFeeds NATURAL JOIN DetailWords NATURAL JOIN PhotoLocation NATURAL JOIN PhotoPath WHERE rs_id > (SELECT max(rs_id)-10 FROM StatusFeeds) ORDER BY rs_id ";

		try {
			// 设置ResultSet是可前后滚动的、可更新的
			statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSet = statement.executeQuery(createCacheRowSet);

			// 构建cacheRowSet
			cachedRowSet = CachedRowSetFactory.getRowSetFactory().createCachedRowSet();
			cachedRowSet.populate(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet);
			ReleaseSource.releaseSource(statement);
		}

		// 自动增长的基础值，现在获得，以便下面使用SQL设置基础值
		int baseValue = 0;
		if (cachedRowSet.first()) {
			baseValue = cachedRowSet.getInt(1);
		}

		/**
		 * 从数据库中删除已经放入CacheRowSet中的数据，这样的好处是，对于客户端删除操作
		 * 可以先检测缓存，如果缓存存在，则删除，但是不在数据库中删除，因为数据库中已经没有
		 * 相关的数据；如果缓存不存在，则只有在数据库中进行删除操作
		 *
		 * 如果下面的操作出错，那么将直接影响整个系统的运行
		 * 如果下面的操作出错，那么将直接影响整个系统的运行
		 * 如果下面的操作出错，那么将直接影响整个系统的运行
		 * 如果下面的操作出错，那么将直接影响整个系统的运行
		 */


		if (cachedRowSet.first()) {
			//从数据库中删除从StatusFeeds获取的数据
			String deleteCached = "DELETE FROM StatusFeeds WHERE rs_id = ?";

			String deleteCached2 = "DELETE FROM DetailWords WHERE rs_id = ?";

			String deleteCached3 = "DELETE FROM PhotoLocation WHERE rs_id = ?";

			String deleteCached4 = "DELETE FROM PhotoPath WHERE rs_id = ?";

			// 下面所需要用到的临时表对象
			PreparedStatement preparedStatement = null;

			/**
			 *  下面这一段删除代码，前期不会影响到性能。后期需要重写！
			 */
			// 设置事务手动提交
			con.setAutoCommit(false);
			try {
				do {
					int rs_id = cachedRowSet.getInt(1);

					// 从StatusFeeds中删除已经存入到缓存中的数据
					preparedStatement = con.prepareStatement(deleteCached);
					preparedStatement.setInt(1, rs_id);
					preparedStatement.execute();
					// 释放资源
					ReleaseSource.releaseSource(preparedStatement);


					// 从DetailWords中删除已经存入到缓存中的数据
					preparedStatement = con.prepareStatement(deleteCached2);
					preparedStatement.setInt(1, rs_id);
					preparedStatement.execute();
					// 释放资源
					ReleaseSource.releaseSource(preparedStatement);


					// 从PhotoLocation中删除已经存入到缓存中的数据
					preparedStatement = con.prepareStatement(deleteCached3);
					preparedStatement.setInt(1, rs_id);
					preparedStatement.execute();
					// 释放资源
					ReleaseSource.releaseSource(preparedStatement);


					// 从PhotoPath中删除已经存入到缓存中的数据
					preparedStatement = con.prepareStatement(deleteCached4);
					preparedStatement.setInt(1, rs_id);
					preparedStatement.execute();
					// 释放资源
					ReleaseSource.releaseSource(preparedStatement);

				} while (cachedRowSet.next());

				// 修改AUTO_INCREMENT基础值
				PreparedStatement rebaseValue;
				String rebaseValueSql = "ALTER TABLE StatusFeeds AUTO_INCREMENT = ?";
				rebaseValue = con.prepareStatement(rebaseValueSql);
				rebaseValue.setInt(1, baseValue);
				rebaseValue.execute();
				// 释放资源
				ReleaseSource.releaseSource(rebaseValue);

				// 所有查询、删除任务顺利完成后提交事务
				con.commit();
			} catch (SQLException e) {
				con.rollback();
				ReleaseSource.releaseSource(preparedStatement, con);
				// 抛出异常
				throw e;
			}finally {
				ReleaseSource.releaseSource(preparedStatement);
			}
		}
		return cachedRowSet;
	}


	/**
	 * 向CachedRowSet内存数据库插入PhotoDesBean的相关数据，最新的数据将会被插入到最后一行
	 *
	 * @param photoDesBean 新上传的照片的相关数据
	 * @return 成功插入CachedRowSet返回true，否则返回false
	 * @throws SQLException
	 */
	public static boolean insertData(PhotoDesBean photoDesBean) throws SQLException {

		CachedRowSet cachedRowSet = StatusRowSetManager.getStatusRowSet();

		// 将数据插入最后一行的下一行
		try {
			cachedRowSet.last();
			cachedRowSet.moveToInsertRow();

			// 更新数据
			cachedRowSet.updateInt(1, photoDesBean.getRs_id());
			cachedRowSet.updateInt(2, photoDesBean.getID());
			cachedRowSet.updateTimestamp(3, photoDesBean.getTime());
			cachedRowSet.updateString(4, photoDesBean.getPhotoClass());
			cachedRowSet.updateString(5, photoDesBean.getPhotoAt());
			cachedRowSet.updateString(6, photoDesBean.getPhotoTopic());
			cachedRowSet.updateInt(7, 0);
			cachedRowSet.updateInt(8, 0);
			cachedRowSet.updateInt(9, 0);
			/**
			 * 设置位置标记位
			 */
			if (photoDesBean.getPhotoLocation() != null) {
				cachedRowSet.updateString(10, "yes");
			} else {
				cachedRowSet.updateString(10, "no");
			}

			if (photoDesBean.getMyWords() != null || photoDesBean.getOlderWords() != null) {
				cachedRowSet.updateString(11, "yes");
			} else {
				cachedRowSet.updateString(11, "no");
			}
			cachedRowSet.updateString(12, photoDesBean.getOlderWords());
			cachedRowSet.updateString(13, photoDesBean.getMyWords());
			cachedRowSet.updateString(14, photoDesBean.getPhotoLocation());
			cachedRowSet.updateString(15, photoDesBean.getViewPhotoPath());
			cachedRowSet.updateString(16, photoDesBean.getDetailPhotoPath());

			// 插入数据
			cachedRowSet.insertRow();
			cachedRowSet.moveToCurrentRow();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return true;
	}


	/**
	 * 删除资源号相关的所有资源
	 *
	 * @param ID    执行删除操作的ID
	 * @param rs_id 需要删除的资源的唯一标记号
	 * @return 从数据库中获取的数据的CacheRowSet对象
	 */
	public static boolean deleteData(int ID, int rs_id) {


		return false;
	}

	/**
	 * 根据所提供的rs_id值返回ID
	 *
	 * @param rs_id 资源标记
	 * @return 如果成功, 返回ID;如果失败,返回-1
	 * @throws SQLException
	 */
	public static int getPermissionId(int rs_id) throws SQLException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		String getIt = "SELECT ID FROM PermissionTable WHERE rs_id = ?";
		try {
			connection = ConnectionFactory.getMySqlConnection();
			preparedStatement = connection.prepareStatement(getIt);
			preparedStatement.setInt(1, rs_id);

			resultSet = preparedStatement.executeQuery();
			if (resultSet.first()) {
				return resultSet.getInt(1);
			}
		} finally {
			ReleaseSource.releaseSource(resultSet, preparedStatement, connection);
		}
		return -1;
	}


	/**
	 * 用于从数据库获取一定数量的数据,标准是rs_id.要么获取标记rs_id以前的数据;要么
	 * 获取标记rs_id以后的数据
	 *
	 * @param rs_id  标记rs_id,用于和数据库rs_id做作比较
	 * @param before 标记获取当前给定rs_id时间前后的数据,如果为真,则获取当前
	 *               标记rs_id以前的数据;如果为假,则获取当前标记rs_id以后的数据
	 */
	public static CachedRowSet selectData(ArrayList<Integer> arrayList, int rs_id, boolean before) throws SQLException {

		PreparedStatement preparedStatement = null;

		Connection connection = null;

		ResultSet resultSet = null;

		CachedRowSet cachedRowSet = null;

		StringBuilder stringBuilder = new StringBuilder();

		// 添加关注的人,用于数据库查询数据限制
		for (Integer integer : arrayList) {
			stringBuilder.append(integer);
			stringBuilder.append(",");
		}

		stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

		// 更新
		if (before) {
			try {
				connection = ConnectionFactory.getMySqlConnection();

				String upDate = "SELECT * FROM StatusFeeds NATURAL JOIN DetailWords NATURAL JOIN PhotoLocation NATURAL JOIN PhotoPath WHERE ID IN (" + stringBuilder + ") AND rs_id > ? ORDER BY rs_id desc LIMIT 10";

				preparedStatement = connection.prepareStatement(upDate);

				preparedStatement.setInt(1, rs_id);

				resultSet = preparedStatement.executeQuery();

				cachedRowSet = CachedRowSetFactory.getRowSetFactory().createCachedRowSet();
				cachedRowSet.populate(resultSet);
			} catch (SQLException e) {
				e.printStackTrace();
				ReleaseSource.releaseSource(cachedRowSet);
				throw e;
			} finally {
				ReleaseSource.releaseSource(resultSet, preparedStatement, connection);
			}
		} else {
			try {
				// 加载更多
				connection = ConnectionFactory.getMySqlConnection();

				String upDate = "SELECT * FROM StatusFeeds NATURAL JOIN DetailWords NATURAL JOIN PhotoLocation NATURAL JOIN PhotoPath WHERE ID IN (" + stringBuilder + ") AND rs_id < ? order by rs_id desc LIMIT 10";

				preparedStatement = connection.prepareStatement(upDate);

				preparedStatement.setInt(1, rs_id);

				resultSet = preparedStatement.executeQuery();
				cachedRowSet = CachedRowSetFactory.getRowSetFactory().createCachedRowSet();
				cachedRowSet.populate(resultSet);
			} catch (SQLException e) {
				e.printStackTrace();
				ReleaseSource.releaseSource(cachedRowSet);
				throw e;
			} finally {
				ReleaseSource.releaseSource(resultSet, preparedStatement, connection);
			}
		}
		return cachedRowSet;
	}

	/**
	 * 将CachedRowSet中的数据同步到数据库中相对应的表中
	 *
	 * @param cachedRowSet 当前需要同步的CachedRowSet对象
	 */
	public static void statusSynchronized(CachedRowSet cachedRowSet) throws SQLException {

		Connection connection = ConnectionFactory.getMySqlConnection();
		int id = cachedRowSet.getInt(2);

		Timestamp timestamp = cachedRowSet.getTimestamp(3);

		String photoClass = cachedRowSet.getString(4);

		String photoAt = cachedRowSet.getString(5);

		String photoTopic = cachedRowSet.getString(6);

		int commentsNumber = cachedRowSet.getInt(7);

		int likesNumber = cachedRowSet.getInt(8);

		int sharesNumber = cachedRowSet.getInt(9);

		String isLocated = cachedRowSet.getString(10);
		String hasDetail = cachedRowSet.getString(11);

		String olderWords = cachedRowSet.getString(12);

		String myWords = cachedRowSet.getString(13);

		String location = cachedRowSet.getString(14);

		String viewPhotoPath = cachedRowSet.getString(15);

		String detailPhotoPath = cachedRowSet.getString(16);

		int rs_id = 0;

		PreparedStatement preparedStatement = null;

		// 向StatusFeeds表中插入数据
		try {
			String insertToStatusFeeds = "INSERT INTO StatusFeeds(ID, time, photo_class, photo_at, photo_topic, comments_number, likes_number, shares_number, IsLocated, hasDetail) VALUES (?,?,?,?,?,?,?,?,?,?)";
			// 设置事务
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(insertToStatusFeeds);
			preparedStatement.setInt(1, id);
			preparedStatement.setTimestamp(2, timestamp);
			preparedStatement.setString(3, photoClass);
			preparedStatement.setString(4, photoAt);
			preparedStatement.setString(5, photoTopic);
			preparedStatement.setInt(6, commentsNumber);
			preparedStatement.setInt(7, likesNumber);
			preparedStatement.setInt(8, sharesNumber);
			preparedStatement.setString(9, isLocated);
			preparedStatement.setString(10, hasDetail);

			//执行
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			// 回滚事务
			connection.rollback();
			ReleaseSource.releaseSource(preparedStatement, connection);
			throw e;
		} finally {
			ReleaseSource.releaseSource(preparedStatement);
		}

		// 临时性结果集
		ResultSet resultSet = null;

		// 获取rs_id
		try {
			String getRsId = "SELECT rs_id FROM StatusFeeds WHERE ID = ? ORDER BY rs_id DESC LIMIT 1";

			preparedStatement = connection.prepareStatement(getRsId);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			resultSet.first();
			// 将数据库生成的rs_id获得
			rs_id = resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			// 回滚事务
			connection.rollback();
			ReleaseSource.releaseSource(resultSet, preparedStatement, connection);
			throw e;
		} finally {
			ReleaseSource.releaseSource(resultSet, preparedStatement);
		}

		// 向DetailWords插入数据
		try {
			String insertToDetailWords = "INSERT INTO DetailWords(rs_id, older_words, my_words) VALUES (?,?,?)";
			preparedStatement = connection.prepareStatement(insertToDetailWords);

			preparedStatement.setInt(1, rs_id);
			preparedStatement.setString(2, olderWords);
			preparedStatement.setString(3, myWords);

			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			// 回滚事务
			connection.rollback();
			ReleaseSource.releaseSource(preparedStatement, connection);
			throw e;
		} finally {
			ReleaseSource.releaseSource(preparedStatement);
		}


		// 如果有位置信息，则PhotoPath中插入数据
		try {
			String insertToPhotoLocation = "INSERT INTO PhotoLocation(rs_id, location) VALUES (?,?)";
			preparedStatement = connection.prepareStatement(insertToPhotoLocation);

			preparedStatement.setInt(1, rs_id);
			preparedStatement.setString(2, location);

			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			// 回滚事务
			connection.rollback();
			ReleaseSource.releaseSource(preparedStatement, connection);
			throw e;
		} finally {
			ReleaseSource.releaseSource(preparedStatement);
		}

		try {
			String insertPhotoPath = "INSERT INTO PhotoPath(rs_id, view_photo,detail_photo) VALUES (?,?,?)";
			preparedStatement = connection.prepareStatement(insertPhotoPath);

			preparedStatement.setInt(1, rs_id);
			preparedStatement.setString(2, viewPhotoPath);
			preparedStatement.setString(3, detailPhotoPath);

			preparedStatement.execute();

			// 提交事务，向数据库中写入成功
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			// 回滚事务
			connection.rollback();
			ReleaseSource.releaseSource(resultSet, preparedStatement, connection);
			throw e;
		} finally {
			ReleaseSource.releaseSource(preparedStatement, connection);
		}
	}


	/**
	 * 更新用户照片的数量，如果isMore是真，便是增加数量；如果是假，就是减少用户照片的数量
	 *
	 * @param ID          需要更新照片数量的用户的ID
	 * @param photoNumber 增加或者减少的照片的数量
	 * @param isMore      减少还是增加的标记
	 * @throws SQLException
	 */
	public static void updatePhotoNumber(int ID, int photoNumber, boolean isMore) throws SQLException {
		Connection connection;
		PreparedStatement preparedStatement = null;

		String updateNumber;
		if (isMore) {
			updateNumber = "UPDATE PhotoSum SET photo_sum_number = photo_sum_number + ? WHERE ID = ?";
		} else {
			updateNumber = "UPDATE PhotoSum SET photo_sum_number = photo_sum_number - ? WHERE ID = ?";
		}
		// 获得数据库连接
		connection = ConnectionFactory.getMySqlConnection();
		try {
			preparedStatement = connection.prepareStatement(updateNumber);
			preparedStatement.setInt(1, photoNumber);
			preparedStatement.setInt(2, ID);
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			ReleaseSource.releaseSource(preparedStatement,connection);
			throw e;
		} finally {
			ReleaseSource.releaseSource(preparedStatement, connection);
		}
	}

}
