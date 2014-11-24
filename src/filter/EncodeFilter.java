package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * author: 康乐
 * time: 2014/7/15
 * function: 给需要的servlet或者jsp提供utf-8编码支持
 */

@WebFilter(filterName = "EncodeFilter",urlPatterns = {"/sign_up","/login","/checkAccount"},dispatcherTypes = {DispatcherType.REQUEST,
		DispatcherType.INCLUDE,DispatcherType.FORWARD,DispatcherType.ERROR,DispatcherType.ASYNC})
public class EncodeFilter implements Filter {
	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

		HttpServletRequest httpServletRequest = (HttpServletRequest)req;

		httpServletRequest.setCharacterEncoding("UTF-8");

		resp.setCharacterEncoding("UTF-8");

		chain.doFilter(req, resp);
	}

	public void init(FilterConfig config) throws ServletException {

	}

}
