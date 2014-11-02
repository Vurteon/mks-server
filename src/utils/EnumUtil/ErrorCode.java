package utils.EnumUtil;

/**
 * author: 康乐
 * time: 2014/8/28
 * function: 提供对客户端错误信息的返回码
 */

public class ErrorCode {

	// 客户端构造的json格式错误
	public static final String JSONFORMATERROR = "formatError";

	// 服务器IO错误
	public static final String IOERROR = "IOERROR";

	// 服务器文件系统构建文件出错
	public static final String MKDIRERROR = "MKDIRERROR";

	// session过期或者不存在
	public static final String SESSIONERROR = "SESSIONERROR";

	// 服务器sql出错
	public static final String SQLERROR = "SQLERROR";

	// multi-part格式错误
	public static final String MULTIPARTERROR = "MULTIPARTERROR";

	// 图片上传成功
	public static final String SUCCESS = "SUCCESS";

	// 请求处理超时
	public static final String TIMEOUT = "TIMEOUT";
}
