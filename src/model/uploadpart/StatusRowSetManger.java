package model.uploadpart;

import beans.main.PhotoDesBean;
import dao.main.CachedRowSetDao;
import utils.db.TimeBuilder;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

/**
 * author: 康乐
 * time: 2014/8/22
 * function: 提供对RowSet的相关操作(增、删、改、查)、将RowSetDao所需要的信息进行封装
 * 并在内存数据库存储数量大于额定数值后向mysql数据库写入一部分数据
 */

public class StatusRowSetManger {

	/**
	 * statusSet的大小，目前为1000，可能会占用2M到6M内存
	 */
	public static final int statusSetMaxSize = 10;

	/**
	 * statusSet当前数量
	 */
	public static int statusCurrentSize;

	/**
	 * 当达到数据数量容量上限时，一次性需要向数据库写入的数据数量
	 */
	public static final int writeOnceNumber = 5;

	// 存储status的RowSet对象
	public static CachedRowSet statusRowSet;

	/**
	 * 临时rs_id。每一个动态都会有一个id，为了保证id的唯一性性，数据库中每次
	 * 新增数据其id号都会自增1，且是主键，目前只能在内存中暂时使用自增的整形
	 * 来获得临时id，向其中存储的时候再由数据库生成rs_id号
	 */
	public static int rs_id = 100000000;

	// 同步锁控制对象
	private static final Object object = new Object();

	public static boolean insertStatus(PhotoDesBean photoDesBean) throws SQLException {

		photoDesBean.setTime(TimeBuilder.getLocalTime());

		photoDesBean.setRs_id(getRs_id());

		// 插入数据时需要保持同步性
		synchronized (object) {
			// 如果数据数量已超出最大容量，那么就向数据库写入数据，此时也需要
			// 保持同步性，所以需要在synchronized块中进行
			if (statusCurrentSize >= statusSetMaxSize){
				statusOverSizeHandler();
				statusCurrentSize -= writeOnceNumber;
			}

			boolean b = CachedRowSetDao.insertData(photoDesBean);

			// 当前数量大小增1，由于是写完后才进行数量增加，所以每一次
			// 需要数据同步的时候都是下一个请求引起，而最后一个请求刚好
			// 达到上限的值
			if (b){
				statusCurrentSize++;
			}
			return b;
		}
	}

	public static void updateStatus() {

	}

	public static void selectStatus() {

	}

	public static void deleteStatus() {

	}


	/**
	 * 当缓存数量达到上限后，则调用此方法从而将缓存数据存入数据库
	 *
	 * @throws SQLException
	 */
	private static void statusOverSizeHandler() throws SQLException {

		// 将删除对CacheRowSet立即执行
		statusRowSet.setShowDeleted(false);
		statusRowSet.first();
		int temp = writeOnceNumber;
		while (temp > 0) {
			CachedRowSetDao.statusInsertSynchronized(statusRowSet);
			// 删除当前行
			statusRowSet.deleteRow();

			//将指针移动到第一行
			statusRowSet.first();
			temp--;
		}

	}


	/**
	 * CachedRowSet的getter
	 *
	 * @return status的CachedRowSet对象
	 */
	public static CachedRowSet getStatusRowSet() {

		return statusRowSet;
	}

	/**
	 * CachedRowSet的setter，并初始化statusCurrentSize
	 *
	 * @param statusRowSet 用于初始化status CachedRowSet的对象
	 */
	public static void setStatusRowSet(CachedRowSet statusRowSet) {
		StatusRowSetManger.statusRowSet = statusRowSet;

		try {
			statusRowSet.first();

			while (statusRowSet.next()) {
				statusCurrentSize++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	/**
	 * 由于rs_id是由数据库生成，且是唯一的，而缓存数据库是为了降低数据库的开销，所以必须
	 * 使用临时的id，待到向数据库写入数据时，才生成真正的id
	 *
	 * 在CachedRowSet缓存中可能存在相同rs_id号的情况，但是这并没有影响，在数据库中的操作
	 * 是依据真实的id进行，而在缓存中根据rs_id进行操作时，可能遇到相同ID和rs_id几率十分小
	 * 可以忽略不计，但是仍然有可能出现问题
	 *
	 * 由于基础数值是从1亿开始的，所以此问题暂时解决
	 *
	 * @return 临时生成的rs_id
	 */
	public static int getRs_id() {
		// 返回rs_id后自增1
		return rs_id++;
	}

}
