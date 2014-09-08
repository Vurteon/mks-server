package utils.db;

import java.sql.Timestamp;
import java.util.Date;

/**
 * author: 康乐
 * time: 2014/8/28
 * function: 更加Date类所提供的long值来得到sql中的Timestamp时间对象
 */
public class TimeBuilder {

	/**
	 * 返回当前时间的Timestamp对象
	 * @return 单签时间的Timestamp对象
	 */
	public static Timestamp getLocalTime() {
		//
		return new Timestamp(new Date().getTime());
	}



}
