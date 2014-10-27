package utils;

import utils.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by leon on 14-10-14.
 */
public class StatusResponseHandler {


	public static void sendStatus(String key, String value,HttpServletResponse httpServletResponse) {

		httpServletResponse.setHeader("Content-Type", "application/json");
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
		}finally {
			releaseIO(pw);
		}

	}



	public static void sendStatus(JSONObject jsonObject,HttpServletResponse httpServletResponse) {

		PrintWriter pw = null;
		try {
			pw = httpServletResponse.getWriter();
			pw.write(jsonObject.toString());
			releaseIO(pw);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			releaseIO(pw);
		}


	}

	private static void releaseIO (PrintWriter printWriter) {
		if (printWriter != null) {
			printWriter.close();
		}
	}


}
