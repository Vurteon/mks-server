package utils.db;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by leon on 2014/8/27.
 */
public class TimeBuilder {

	public static final Date time = new Date();

	public static Timestamp getLocalTime() {
		return new Timestamp(time.getTime());
	}



}
