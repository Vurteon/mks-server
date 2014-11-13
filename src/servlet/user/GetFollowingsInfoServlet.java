package servlet.user;

import model.user.UserInfoManger;
import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;
import utils.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * author:康乐
 * time:2014/11/3
 * function:向客户端发送用户信息，包括：用户的名字、头像（两种格式）、个性签名
 */

@WebServlet(name = "GetFollowingsInfoServlet",urlPatterns = "/get_followings_info")
public class GetFollowingsInfoServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession httpSession = request.getSession(false);

		HashSet<Integer> followings = (HashSet<Integer>)httpSession.getAttribute("followings");

		// 如果没有关注任何人，那么直接返回，不需要更新
		if (followings.size() == 0) {
			StatusResponseHandler.sendStatus("status", ErrorCode.NODATA,response);
			return ;
		}

		JSONArray usersInfo;

		try {
			usersInfo = UserInfoManger.getUsersInfo(followings.iterator());
		} catch (SQLException e) {
			e.printStackTrace();
			StatusResponseHandler.sendStatus("status",ErrorCode.SQLERROR,response);
			return ;
		}

		// 向客户端发送数据
		StatusResponseHandler.sendStatus(usersInfo,response,true);

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}
}
