package servlet.phone.index;

import beans.index.UserAccountBean;
import dao.exception.NoSuchIDException;
import model.index.LoginUser;
import utils.GetPostContent;
import utils.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author: 康乐
 * time: 2014/8/1
 * function: 提供手机端用户登录的检查和session的设置，基于http连接
 */

@WebServlet(name = "PhoneLoginServlet",urlPatterns = "/phone_login")
public class PhoneLoginServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String userAccountInfo = GetPostContent.getPostContent(request);

		if (userAccountInfo == null) {
			response.sendError(404);
			return ;
		}

		UserAccountBean userAccountBean;
		JSONObject userAccountJson = new JSONObject(userAccountInfo);

		if (userAccountJson.has("email") && userAccountJson.has("password")) {
			userAccountBean = new UserAccountBean();
			userAccountBean.setEmail(userAccountJson.getString("email"));
			userAccountBean.setPassword(userAccountJson.getString("password"));
		}else {
			response.sendError(404);
			return ;
		}

		JSONObject jsonObject = LoginUser.isAccountPassed(userAccountBean);
		if (jsonObject.getString("isPassed").equals("no")) {
			response.getWriter().write(jsonObject.toString());
			response.getWriter().close();
			return ;
		} else {
			try {
				if(LoginUser.setSession(userAccountBean,request.getSession())) {
					response.getWriter().write(jsonObject.toString());
				}else {
					response.getWriter().write(new JSONObject("{'server':'error'}").toString());
				}
			} catch (NoSuchIDException e) {
				e.printStackTrace();
			}
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(404);
	}
}
