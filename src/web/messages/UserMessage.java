package web.messages;

import com.sun.deploy.net.HttpResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by dahy on 2014/7/13.
 */
public class UserMessage {

    public static void userExistMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("registerMessage", "用户已存在");
        request.getRequestDispatcher("RegisterMessage.jsp").forward(request,response);
    }
    public static void loginFailMessage(){

    }
    public static void loginSuccessMessage(){

    }
    public static void registerSuccessMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("registerMessage", "注册成功");
        request.getRequestDispatcher("RegisterMessage.jsp").forward(request,response);
    }
    public static void registerFailMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("registerMessage", "注册失败！！！");
        request.getRequestDispatcher("RegisterMessage.jsp").forward(request,response);
    }
    public static void errorMessage(HttpServletResponse response){

    }
}
