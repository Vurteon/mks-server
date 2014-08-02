package utils.db;

import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.SQLException;

/**
 * author: 康乐
 * time: 2014/8/1
 * function: 提供RowSetFactory
 */
public class GetRowSetFactory {

	private static RowSetFactory rowSetFactory;

	public static RowSetFactory getRowSetFactory() {

		if (rowSetFactory == null) {
			try {
				rowSetFactory = RowSetProvider.newFactory();
			} catch (SQLException e) {
				System.err.println("严重错误，RowSetFactory创建出错！");
				e.printStackTrace();
			}
		}
		return rowSetFactory;
	}


}
