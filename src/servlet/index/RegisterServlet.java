package servlet.index;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author: 康乐
 * time: 2014/7/15
 * change-time：2014/7/28
 * function: 注册用户、初始化相关数据
 */
@WebServlet(name = "RegisterServlet",urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {



























//        request.setCharacterEncoding("UTF-8");
//        User user = getUserRegisterForm(request);                             //获取注册表单数据
//        String registerMessage = new UserSever().register(user);              //注册，并获取注册返回消息
//
//        if(registerMessage.equals(USER_EXIST)){                              //转到用户注册消息页
//            userExistMessage(request, response);
//            return;
//        }
//        if(registerMessage.equals(REGISTER_SUCCESS)){
//            registerSuccessMessage(request, response);
//            return;
//        }
//        if(registerMessage.equals(REGISTER_FAIL)){
//            registerFailMessage(request, response);
//            return;
//        }
//        errorMessage(response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
