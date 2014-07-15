package web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Enumeration;

/**
 * author: 康乐
 * time: 2014/7/15
 * function: 检测用户名是否存在，并返回
 */
@WebServlet("/isEmailUsed")
public class IsEmailUsedServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


//		System.out.println(request.getHeaderNames());

		Enumeration<String> em = request.getHeaderNames();


		while (em.hasMoreElements()){

			String header = em.nextElement();

			String value = request.getHeader(header);

			System.out.println(header + " : " + value);
		}



		BufferedReader br = new BufferedReader(new InputStreamReader(
				request.getInputStream(),"utf-8"));

		StringBuilder sb = new StringBuilder();

		String temp;
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		br.close();


//		response.setHeader("Access-Control-Allow-Origin","https://localhost:8443/isEmailUsed");
		response.setHeader("Content-Type","application/json");

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"utf-8"));

		bw.write("'ok':'阿萨德'");

		bw.close();




//		JSONObject jsonObject = new JSONObject(sb.toString());

		System.out.println(sb);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
