package servlet.account;

import beans.index.UserAccountBean;
import dao.exception.NoSuchIDException;
import model.account.LoginUserHandler;
import utils.EnumUtil.ErrorCode;
import utils.FormatCheckManager;
import utils.JsonUtils;
import utils.RequestInfoUtils;
import utils.StatusResponseHandler;
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

@WebServlet(name = "LoginServlet",urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 获得上传来的json数据
		String userAccountInfo = RequestInfoUtils.getPostContent(request);

		if (userAccountInfo == null) {
			response.sendError(404);
			return ;
		}

		UserAccountBean userAccountBean;
		JSONObject userAccountJson = new JSONObject(userAccountInfo);

		// 判断数据格式
		if (userAccountJson.has("account") && userAccountJson.has("password")) {

			if (FormatCheckManager.checkAccount() && FormatCheckManager.checkPassword()) {
				userAccountBean = new UserAccountBean();
				userAccountBean.setAccount(userAccountJson.getString("account"));
				userAccountBean.setPassword(userAccountJson.getString("password"));
			}else {
				StatusResponseHandler.sendStatus("accountResult", ErrorCode.JSONFORMATERROR, response,true);
				return ;
			}
		}else {
			StatusResponseHandler.sendStatus("accountResult", ErrorCode.JSONFORMATERROR, response,true);
			return ;
		}

		// 检测密码是否正确
		boolean isPassed = LoginUserHandler.isAccountPassed(userAccountBean);
		if (isPassed) {
			// 如果正确，那么就进行session的设置
			try {
				HttpSession httpSession = request.getSession(true);

				if(LoginUserHandler.setSession(userAccountBean, httpSession)) {
					// 向客户端返回sessionID
					JSONObject responseInfo = JsonUtils.getJsonObject("{'accountResult':'success';'JSESSIONID':" +httpSession.getId() +  "}");
					StatusResponseHandler.sendStatus(responseInfo,response,true);
				}else {
					StatusResponseHandler.sendStatus("accountResult","error",response,true);
				}
			} catch (NoSuchIDException e) {
				e.printStackTrace();
			}
		} else {
			// 密码错误，返回消息
			StatusResponseHandler.sendStatus("accountResult","dataWrong",response,true);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 直接返回错误代码
		response.sendError(404);
	}
}
