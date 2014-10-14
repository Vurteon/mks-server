package utils;

import utils.json.JSONException;
import utils.json.JSONObject;

/**
 *  author: 康乐
 *  time: 2014/8/21
 *  function: 根据一段字符串生成一个相应的json对象
 */
public class JsonUtils {

	public static JSONObject getJsonObject(String jsonString) {

		JSONObject jsonObject = null;

		try {
			jsonObject = new JSONObject(jsonString);
		}catch (JSONException j) {
			// 看以后的需要而改
		}
		return jsonObject;
	}
}
