package utils.EnumUtil;

import utils.CreateJson;
import utils.json.JSONObject;

/**
 * author: 康乐
 * time: 2014/8/28
 * function: 提供ErrorJson对象
 */

public class ErrorCodeJson {

	// IO错误代码的json对象
	public static final JSONObject IOERROR = CreateJson.getJsonObject("{'errorCode':" + ErrorCode.IOERROR + "}");

	// 服务器文件系统错误代码的json对象
	public static final JSONObject MKDIRERROR = CreateJson.getJsonObject("{'errorCode':" + ErrorCode.MKDIRERROR + "}");

	// Session过期或者不存在错误
	public static final JSONObject SESSIONERROR = CreateJson.getJsonObject("{'errorCode':" + ErrorCode.SESSIONERROR + "}");

}
