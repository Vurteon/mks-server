package dao.main;

import beans.main.PhotoDesBean;
import model.uploadpart.StatusRowSetManger;
import utils.db.*;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;

/**
 * author: 康乐
 * time: 2014/8/27-31
 * function: 提供对内存数据库CachedRowSet的增、删、改、查操作
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
		String createCacheRowSet = "SELECT * FROM StatusFeeds NATURAL JOIN DetailWords NATURAL JOIN PhotoLocation NATURAL JOIN PhotoPath ORDER BY time DESC LIMIT 1000";

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
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		}





		if (cachedRowSet.first()) {

			/**
			 * 从数据库中删除已经放入CacheRowSet中的数据，这样的好处是，对于客户端删除操作
			 * 可以先检测缓存，如果缓存存在，则删除，但是不在数据库中删除，因为数据库中已经没有
			 * 相关的数据；如果缓存不存在，则只有在数据库中进行删除操作
			 */

			// 从数据库中删除从StatusFeeds获取的数据
			String deleteCached = "delete from StatusFeeds where rs_id = ?";

			String deleteCached2 = "delete from DetailWords where rs_id = ?";

			String deleteCached3 = "delete from PhotoLocation where rs_id = ?";

			String deleteCached4 = "delete from PhotoPath where rs_id = ?";

			// 设置事务
			con.setAutoCommit(false);
			do {

				PreparedStatement preparedStatement = null;
				int rs_id = cachedRowSet.getInt(1);
				try {
					preparedStatement = con.prepareStatement(deleteCached);
					preparedStatement.setInt(1,rs_id);
					preparedStatement.execute();
				}catch (SQLException e){
					con.rollback();
					e.printStackTrace();
					throw e;
				}finally {
					ReleaseSource.releaseSource(preparedStatement);
				}


				try {
					preparedStatement = con.prepareStatement(deleteCached2);
					preparedStatement.setInt(1,rs_id);
					preparedStatement.execute();
				}catch (SQLException e) {
					con.rollback();
					e.printStackTrace();
					throw e;
				}finally {
					ReleaseSource.releaseSource(preparedStatement);
				}

				try {
					preparedStatement = con.prepareStatement(deleteCached3);
					preparedStatement.setInt(1,rs_id);
					preparedStatement.execute();
				}catch (SQLException e) {
					con.rollback();
					e.printStackTrace();
					throw e;
				}finally {
					ReleaseSource.releaseSource(preparedStatement);
				}

				try {
					preparedStatement = con.prepareStatement(deleteCached4);
					preparedStatement.setInt(1,rs_id);
					preparedStatement.execute();
				}catch (SQLException e) {
					con.rollback();
					e.printStackTrace();
					throw e;
				}finally {
					ReleaseSource.releaseSource(preparedStatement);
				}
			}while (cachedRowSet.next());
			// 如果所有操作均完成，则提交事务
			con.commit();
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

		CachedRowSet cachedRowSet = StatusRowSetManger.getStatusRowSet();


		// 将数据插入最后一行的下一行
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

		if (photoDesBean.getPhotoLocation() != null) {
			cachedRowSet.updateString(14, photoDesBean.getPhotoLocation());
			cachedRowSet.updateString(10, "yes");
		} else {
			cachedRowSet.updateString(10, "no");
		}

		if (photoDesBean.getMyWords() != null || photoDesBean.getOlderWords() != null) {

			cachedRowSet.updateString(12, photoDesBean.getOlderWords());
			cachedRowSet.updateString(13, photoDesBean.getMyWords());

			cachedRowSet.updateString(11, "yes");
		} else {
			cachedRowSet.updateString(11, "no");
		}


		cachedRowSet.updateString(15, photoDesBean.getViewPhotoPath());
		cachedRowSet.updateString(16, photoDesBean.getDetailPhotoPath());


		// 插入数据
		cachedRowSet.insertRow();
		cachedRowSet.moveToCurrentRow();
		return true;
	}


	public static void deleteData() {

	}

	public static void updateData() {

	}

	public static void selectData() {

	}

	/**
	 * 将CachedRowSet中的数据同步到数据库中相对应的表中
	 *
	 * @param cachedRowSet 当前需要同步的CachedRowSet对象
	 */
	public static void statusInsertSynchronized(CachedRowSet cachedRowSet) throws SQLException {


		int id = cachedRowSet.getInt(1);

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

		Connection connection = null;

		// 向StatusFeeds表中插入数据
		try {

			String insertToStatusFeeds = "INSERT INTO StatusFeeds(ID, time, photo_class, photo_at, photo_topic, comments_number, likes_number, shares_number, IsLocated, hasDetail) VALUES (?,?,?,?,?,?,?,?,?,?)";

			connection = ConnectionFactory.getMySqlConnection();

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
		} finally {
			ReleaseSource.releaseSource(preparedStatement);
		}

		// 临时性结果集
		ResultSet resultSet = null;

		// 获取rs_id
		try {
			String getRsId = "SELECT rs_id FROM StatusFeeds WHERE ID = ?";

			preparedStatement = connection.prepareStatement(getRsId);

			// 这里的断言以后需要去掉
			assert preparedStatement != null;
			preparedStatement.setInt(1, id);

			resultSet = preparedStatement.executeQuery();

			resultSet.next();

			rs_id = resultSet.getInt(1);

		} catch (SQLException e) {
			e.printStackTrace();
			// 回滚事务
			connection.rollback();
		} finally {
			ReleaseSource.releaseSource(resultSet, preparedStatement);
		}


		// 向DetailWords插入数据
		if (olderWords != null || myWords != null) {
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
			} finally {
				ReleaseSource.releaseSource(preparedStatement);
			}
		}


		// 如果有位置信息，则PhotoPath中插入数据
		if (location != null) {

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
			} finally {
				ReleaseSource.releaseSource(preparedStatement);
			}
		}

		try {
			String insertPhotoPath = "INSERT INTO PhotoPath(rs_id, phone_view_photo,phone_detail_photo) VALUES (?,?,?)";

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
		} finally {
			ReleaseSource.releaseSource(preparedStatement);
		}
	}


}
