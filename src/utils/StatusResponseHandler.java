package utils;

import utils.json.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by leon on 14-10-14.
 */
public class StatusResponseHandler {


	public static void sendStatus(String key, String value,HttpServletResponse httpServletResponse) {

		httpServletResponse.setHeader("Content-Type", "application/json");




	}


	public static void sendStatus(String key, String value, HttpServletResponse httpServletResponse,boolean releaseIO) {

	}



	public static void sendStatus(JSONObject jsonObject,HttpServletResponse httpServletResponse,boolean releaseIO) {

	}




}
