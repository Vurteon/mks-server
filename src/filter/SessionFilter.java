package filter;

import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author: 康乐
 * time: 2014/9/4
 * function: 检查是否存在session,如果不存在,则向客户端发送错误代码,并返回
 */

@WebFilter(filterName = "SessionFilter",urlPatterns = {"/UploadPhoto","/LoadStatus","/get_user_info","/get_three_number","/get_followings_info"},dispatcherTypes = {DispatcherType.REQUEST,
		DispatcherType.INCLUDE,DispatcherType.FORWARD,DispatcherType.ERROR,DispatcherType.ASYNC})
public class SessionFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

		HttpServletRequest httpServletRequest = (HttpServletRequest)req;

		// 如果不存在session，返回给客户端错误代码，表示session可能
		// 已经过期，需要重新登录
		if (httpServletRequest.getSession(false) == null) {
			StatusResponseHandler.sendStatus("status",
					ErrorCode.SESSIONERROR, (HttpServletResponse) resp);
			System.out.println("被拒绝");
			return;
		}
		System.out.println("通过！");
		chain.doFilter(req, resp);
	}

	public void init(FilterConfig config) throws ServletException {

	}

}
