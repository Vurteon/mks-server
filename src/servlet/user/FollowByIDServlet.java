package servlet.user;

import dao.user.UserInfoDao;
import model.notify.NotifyCacheManager;
import model.user.UserReaManager;
import utils.EnumUtil.ErrorCode;
import utils.EnumUtil.NotifyType;
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
import java.sql.SQLException;
import java.util.Random;

/**
 * author：康乐
 * time：2014/11/17
 * function：关注某人
 */

@WebServlet(name = "FollowByIDServlet",urlPatterns = "/follow_by_id")
public class FollowByIDServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession httpSession = request.getSession(false);

		String idsInfo = RequestInfoUtils.getPostContent(request);

		JSONObject jsonObject = new JSONObject(idsInfo);

		int hisID;


		if (jsonObject.has("his_id")) {
			hisID = jsonObject.getInt("his_id");
		}else {
			StatusResponseHandler.sendStatus("status", ErrorCode.JSONFORMATERROR, response);
			return ;
		}

		// 进行记录，如果成功；返回给客户端成功的信息；如果失败，必然是sql异常，返回给客户端
		try {
			if (UserReaManager.followByID((Integer) httpSession.getAttribute("ID"), hisID)) {
				StatusResponseHandler.sendStatus("status", ErrorCode.SUCCESS, response);
				addMessage((Integer) httpSession.getAttribute("ID"),hisID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			StatusResponseHandler.sendStatus("status", ErrorCode.SQLERROR, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().write("follow_by_id!");
		response.getWriter().close();
	}

	private void addMessage(int ID, int hisID) throws SQLException {
		/**
		 * 将消息放入推送map
		 */
		String name = UserInfoDao.getUserName(ID);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("content",name + "关注了你");
		jsonObject.put("class", NotifyType.FOLLOW);
		NotifyCacheManager.putMessage(hisID, jsonObject.toString());
	}
}
