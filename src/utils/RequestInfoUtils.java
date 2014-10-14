package utils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

/**
 *
 */

public class RequestInfoUtils {

	/**
	 * 打印当前request的请求头的所有信息
	 * @param request 当前请求的request对象
	 */
	public static void showHeaderContent(HttpServletRequest request) {
		Enumeration e = request.getHeaderNames();
		while (e.hasMoreElements()) {

			String name = e.nextElement().toString();

			System.out.println(name + " : " + request.getHeader(name));

		}
	}


	/**
	 * 将当前post请求的请求正文以String对象返回，其中的结束符号都将会被抛去
	 *
	 * 十分需要注意的是，内容被获取后不能再使用相关方法从输入流中继续获取内容，一般在测试的时候使用
	 *
	 * @param request post请求的request对象
	 * @return post请求，则返回当前请求的正文；如果不存在正文则返回null
	 */

	public static String getPostContent(HttpServletRequest request) {

		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		BufferedReader br;

		StringBuilder sb = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					request.getInputStream(), "utf-8"));
			sb = new StringBuilder();
			String temp;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 如果内容为空，就返回null
		// 上面的注释简直SB！
		if (sb == null){
			return null;
		}
		return sb.toString();
	}
}
