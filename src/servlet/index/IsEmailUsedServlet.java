package servlet.index;

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
 * change-time：2014/7/28
 * function: 检测用户名(邮箱)是否存在，并返回json数据
 */
@WebServlet(name = "isEmailUsedFilter",urlPatterns = "/isEmailUsed")
public class IsEmailUsedServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


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
