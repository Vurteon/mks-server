package dao.user;

import utils.db.CachedRowSetFactory;
import utils.db.ConnectionFactory;
import utils.db.ReleaseSource;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by leon on 2014/11/3.
 */
public class UserInfoDao {

	/**
	 * 传入用户的ID号，从数据库·获得用户的name、头像（两种格式）、个性化签名
	 * @param IDs 需要获得信息的IDs
	 * @return 拥有用户个人信息的CachedRowSet
	 */
	public static CachedRowSet getUserInfo (Iterator<Integer> IDs) throws SQLException {

		Connection con = ConnectionFactory.getMySqlConnection();

		PreparedStatement ps = null;
		ResultSet resultSet = null;
		CachedRowSet cachedRowSet = null;

		StringBuilder stringBuilder = new StringBuilder();

		Integer id;

		while (IDs.hasNext()) {
			id = IDs.next();
			stringBuilder.append(id);
			stringBuilder.append(",");
		}

		stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

		String getUsersInfo = "select email,name,brief_intro,main_head_photo,home_head_photo from UserInfo natural join UserProfile where ID in ( " + stringBuilder + " )";

		try {
			ps = con.prepareStatement(getUsersInfo);
			resultSet = ps.executeQuery();

			cachedRowSet = CachedRowSetFactory.getRowSetFactory().createCachedRowSet();
			cachedRowSet.populate(resultSet);
		} catch (SQLException e) {
			ReleaseSource.releaseSource(cachedRowSet);
			e.printStackTrace();
			throw e;
		}finally {
			ReleaseSource.releaseSource(resultSet);
			ReleaseSource.releaseSource(ps);
		}
		return cachedRowSet;
	}




}
