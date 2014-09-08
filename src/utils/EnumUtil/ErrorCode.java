package utils.EnumUtil;

/**
 * author: 康乐
 * time: 2014/8/28
 * function: 提供对客户端错误信息的返回码
 */

public class ErrorCode {

	// 客户端构造的json格式错误
	public static final int JSONFORMATERROR = 0;

	// 服务器IO错误
	public static final int IOERROR = 1;

	// 服务器文件系统构建文件出错
	public static final int MKDIRERROR = 2;

	// session过期或者不存在
	public static final int SESSIONERROR = 3;

	// 服务器sql出错
	public static final int SQLERROR = 4;

	// 客户端json不满足格式要求
	public static final int JSONERROR = 5;
}
