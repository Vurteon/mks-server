package web.message;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dahy on 2014/7/13.
 */
public class UserMessage {

    public static void userExistMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {                               //email已存在消息反馈
        request.setAttribute("registerMessage", "用户已存在");                    //将注册结果消息存入注册消息域
        request.getRequestDispatcher("RegisterMessage.jsp").forward(request,response);             //转入注册界面
    }

    public static void registerSuccessMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("registerMessage", "注册成功");
        request.getRequestDispatcher("RegisterMessage.jsp").forward(request,response);             //转入主页
    }
    public static void registerFailMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("registerMessage", "注册失败！！！");
        request.getRequestDispatcher("RegisterMessage.jsp").forward(request,response);              //转入注册界面
    }

    public static void loginFailMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {                               //登录错误消息反馈
        request.setAttribute("loginMessage", "email或者密码错误");                //将登录结果消息存入登录消息域
        request.getRequestDispatcher("LoginMessage.jsp").forward(request,response);             //转入登录界面
    }
    public static void loginSuccessMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("loginMessage", "登录成功");
        request.getRequestDispatcher("LoginMessage.jsp").forward(request,response);            //转入主页
    }
    public static void logoutMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("logoutMessage","注销成功");
        request.getRequestDispatcher("").forward(request, response);            //转入主页
    }
    public static void errorMessage(HttpServletResponse response){

    }
}
