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

		BufferedReader br = new BufferedReader(new InputStreamReader(
				request.getInputStream(),"utf-8"));

		StringBuilder sb = new StringBuilder();

		String temp;
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		br.close();


		response.setHeader("Content-Type","application/json");

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"utf-8"));

		/**
		 * 这里暂时写的是没有被注册，以后再修改这个地方
		 */
		bw.write("{'isUsed':'no'}");

		bw.close();


	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
