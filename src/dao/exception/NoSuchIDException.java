package dao.exception;

/**
 * author: 康乐
 * time: 2014/7/31
 * function: 功能如其名，当有时数据库查询时本该存在ID，实际却不存在时抛出该异常
 */
public class NoSuchIDException extends Exception{

	public NoSuchIDException() {
		super("No such a ID");
	}
}
