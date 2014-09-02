package servlet.phone.index;

import beans.index.UserAccountBean;
import dao.exception.NoSuchIDException;
import model.index.LoginUser;
import utils.RequestInfoUtils;
import utils.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * author: 康乐
 * time: 2014/8/1
 * function: 提供手机端用户登录的检查和session的设置，基于http连接
 */

@WebServlet(name = "PhoneLoginServlet",urlPatterns = "/phone_login")
public class PhoneLoginServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 获得上传来的json数据
		String userAccountInfo = RequestInfoUtils.getPostContent(request);

		if (userAccountInfo == null) {
			response.sendError(404);
			return ;
		}

		UserAccountBean userAccountBean;
		JSONObject userAccountJson = new JSONObject(userAccountInfo);

		// 判断其中是否存在合理数据
		if (userAccountJson.has("email") && userAccountJson.has("password")) {
			userAccountBean = new UserAccountBean();
			userAccountBean.setEmail(userAccountJson.getString("email"));
			userAccountBean.setPassword(userAccountJson.getString("password"));
		}else {
			response.sendError(404);
			return ;
		}

		// 检测数据是否正确
		JSONObject jsonObject = LoginUser.isAccountPassed(userAccountBean);
		if (jsonObject.getString("isPassed").equals("no")) {
			response.getWriter().write(jsonObject.toString());
			response.getWriter().close();
		} else {

			// 如果正确，那么就进行session的设置
			try {
				HttpSession httpSession = request.getSession(true);

				if(LoginUser.setSession(userAccountBean,httpSession)) {
					// 向客户端返回sessionID
					jsonObject.append("JSESSIONID",httpSession.getId());
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
		// 直接返回错误代码
		response.sendError(404);
	}
}
