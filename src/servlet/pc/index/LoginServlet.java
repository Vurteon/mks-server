package servlet.pc.index;


import beans.index.UserAccountBean;
import dao.exception.NoSuchIDException;
import model.index.LoginUser;
import utils.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author: 曾陶
 * change-author:康乐
 * change-time: 2014/7/28
 * change-time: 2014/7/30
 * function: 检测用户登录、初始化相关数据
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String email = request.getParameter("email");
		String password = request.getParameter("password");

		UserAccountBean userAccountBean;

		if (email != null && password != null) {
			userAccountBean = new UserAccountBean();
			userAccountBean.setEmail(email);
			userAccountBean.setPassword(password);
		} else {
			response.sendError(404);
			return;
		}

		JSONObject jsonObject = LoginUser.isAccountPassed(userAccountBean);
		if (jsonObject.getString("isPassed").equals("no")) {
			response.getWriter().write(jsonObject.toString());
			response.getWriter().close();
			return;
		} else {
			try {
				if (LoginUser.setSession(userAccountBean, request.getSession())) {
					response.sendRedirect("http://localhost:8080/main.jsp");
				} else {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} catch (NoSuchIDException e) {
				e.printStackTrace();
			}
		}


	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendError(404);
	}
}
