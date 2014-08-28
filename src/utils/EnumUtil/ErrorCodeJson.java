package utils.EnumUtil;

import utils.CreateJson;
import utils.json.JSONObject;

/**
 * Created by leon on 2014/8/22.
 */
public class ErrorCodeJson {

	public static final JSONObject IOERROR = CreateJson.getJsonObject("{'errorCode':" + ErrorCode.IOERROR + "}");

	public static final JSONObject MKDIRERROR = CreateJson.getJsonObject("{'errorCode':" + ErrorCode.MKDIRERROR + "}");



}
