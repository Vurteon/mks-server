package servlet.pc.index;

import beans.index.RegisterInfoBean;
import model.index.RegisteUser;
import utils.GetPostContent;
import utils.db.DefaultPicSource;
import utils.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

/**
 * author: 康乐
 * time: 2014/7/15
 * change-time：2014/7/28
 * change-time: 2014/7/30
 * function: 注册用户、初始化相关数据
 */
@WebServlet(name = "RegisterServlet",urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


	    // 向数据库添加注册信息
	    RegisterInfoBean registerInfoBean = new RegisterInfoBean();
	    String name = request.getParameter("name");
	    String email = request.getParameter("email");
	    String password = request.getParameter("password");
	    JSONObject jsonObject = null;

	    if (name != null && email != null && password != null) {
		    registerInfoBean.setName(name);
		    registerInfoBean.setEmail(email);
		    registerInfoBean.setPassword(password);
		    jsonObject = RegisteUser.registeUser(registerInfoBean);
	    }else {
		    response.sendError(404);
		    return ;
	    }


	    // 由于在注册前就已经完全检查了数据的正确性，所以这里返回的一定是record的信息
	    // 如果是返回了email已经存在的信息，必然是有人构建了一次非正常请求，

	    // 如果返回值是{"isExist":"yes"}表示这是一个非正常请求，需要记录
		if (jsonObject.has("isExist")) {
			System.err.println("发生了一次非正常请求,ip地址为:" + request.getRemoteAddr());
			response.sendError(404);
			return ;
		}

		// 编写用户的session
	    HttpSession httpSession = request.getSession(true);

	    // 由于刚注册，所以只有name是用户定义的，其余的均是默认值
	    httpSession.setAttribute("name", registerInfoBean.getName());

	    httpSession.setAttribute("bg_pic", DefaultPicSource.DEFAULT_BG_PIC);
	    httpSession.setAttribute("hot_pic_area",DefaultPicSource.DEFAULT_HOT_PIC_AREA);
	    httpSession.setAttribute("home_head_pic",DefaultPicSource.DEFAULT_HOME_HEAD_PIC);
	    httpSession.setAttribute("home_head_bg_pic",DefaultPicSource.DEFAULT_HOME_HEAD_BG_PIC);
	    httpSession.setAttribute("main_head_pic",DefaultPicSource.DEFAULT_MAIN_HEAD_PIC);
		httpSession.setAttribute("main_head_bg_pic",DefaultPicSource.DEFAULT_MAIN_HEAD_BG_PIC);

	    request.getRequestDispatcher("/main.jsp").forward(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
