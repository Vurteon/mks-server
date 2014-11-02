package utils;

import utils.json.JSONArray;
import utils.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * author:康乐
 * time:2014/11/2
 * function:向客户端发送服务器所提供的返回数据
 *
 */

public class StatusResponseHandler {

	/**
	 * 向客户端发送单个键值对，一般用于表示状态信息。发送完成，释放输出资源
	 * @param key key
	 * @param value value
	 * @param httpServletResponse 客户端所对应的httpServletResponse对象
	 */
	public static void sendStatus(String key, String value, HttpServletResponse httpServletResponse) {

		httpServletResponse.setHeader("Content-Type", "application/json");
		httpServletResponse.setCharacterEncoding("UTF-8");
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("'");
		sb.append(key);
		sb.append("'");
		sb.append(":");
		sb.append("'");
		sb.append(value);
		sb.append("'");
		sb.append("}");

		PrintWriter pw = null;

		try {
			pw = httpServletResponse.getWriter();
			pw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			releaseIO(pw);
		}

	}


	/**
	 * 向客户端发送一个json对象，用户可选发送完成后是否释放输出资源
	 * @param jsonObject    需要发送的json对象
	 * @param httpServletResponse   客户端所对应的httpServletResponse对象
	 * @param isRelease     是否需要释放输出资源。如果为true，释放资源；否则，不释放资源，以便继续发送其他返回数据
	 */
	public static void sendStatus(JSONObject jsonObject, HttpServletResponse httpServletResponse, boolean isRelease) {

		httpServletResponse.setHeader("Content-Type", "application/json");
		httpServletResponse.setCharacterEncoding("UTF-8");
		PrintWriter pw = null;
		try {
			pw = httpServletResponse.getWriter();
			pw.write(jsonObject.toString());
			if (isRelease)
				releaseIO(pw);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (isRelease)
				releaseIO(pw);
		}
	}


	/**
	 * 向客户端发送一个json对象，用户可选发送完成后是否释放输出资源
	 * @param jsonArray    需要发送的json对象数组
	 * @param httpServletResponse   客户端所对应的httpServletResponse对象
	 * @param isRelease     是否需要释放输出资源。如果为true，释放资源；否则，不释放资源，以便继续发送其他返回数据
	 */
	public static void sendStatus(JSONArray jsonArray, HttpServletResponse httpServletResponse, boolean isRelease) {
		httpServletResponse.setHeader("Content-Type", "application/json");
		httpServletResponse.setCharacterEncoding("UTF-8");
		PrintWriter pw = null;
		try {
			pw = httpServletResponse.getWriter();
			pw.write(jsonArray.toString());
			if (isRelease)
				releaseIO(pw);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (isRelease)
				releaseIO(pw);
		}
	}

	/**
	 * 释放参数所指向的输出资源
	 * @param printWriter   输出资源
	 */
	private static void releaseIO(PrintWriter printWriter) {
		if (printWriter != null) {
			printWriter.close();
		}
	}


}
