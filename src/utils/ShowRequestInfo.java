package utils;

import com.sun.deploy.net.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Created by leon on 2014/7/30.
 */
public class ShowRequestInfo {

	public static void showHeaderContent(HttpServletRequest request) {
		Enumeration e = request.getHeaderNames();
		while (e.hasMoreElements()) {

			String name = e.nextElement().toString();

			System.out.println(name + " : " + request.getHeader(name));

		}
	}



}
