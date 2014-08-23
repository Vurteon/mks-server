package utils;

import utils.json.JSONObject;

/**
 *  author: 康乐
 *  time: 2014/8/21
 *  function: 根据一段字符串生成一个相应的json对象
 */
public class CreateJson {

	public static JSONObject getJsonObject(String jsonString) {

		JSONObject jsonObject = new JSONObject(jsonString);
		return jsonObject;
	}
}
