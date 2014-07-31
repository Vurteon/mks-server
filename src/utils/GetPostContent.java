package utils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * author: 康乐
 * time: 2014/7/28
 * function: 传入参数后获取输入流，将其中的内容返回
 * attention: 内容被获取后不能再使用相关方法从输入流中继续获取内容，一般在测试的时候使用
 */
public class GetPostContent {

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
