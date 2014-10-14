package model.status;

import utils.EnumUtil.ErrorCodeJson;
import utils.json.JSONArray;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * author: 康乐
 * time: 2014/9/8
 * function: 异步向客户端发送客户端所请求的相应status数据
 */

public class LoadStatus implements Runnable {
	// 包含当前用户所有关注的人的id
	private HashSet<Integer> followings;
	// 资源标志
	private int rs_id;
	// 标记加载更多内容请求还是更新请求
	private boolean before;
	// 异步请求对象
	private AsyncContext asyncContext;

	public LoadStatus(AsyncContext asyncContext, HashSet<Integer> followings, int rs_id, boolean before) {
		this.asyncContext = asyncContext;
		this.followings = followings;
		this.rs_id = rs_id;
		this.before = before;
	}

	@Override
	public void run() {
		try {
			JSONArray statusArray = StatusRowSetManager.selectStatus(followings, rs_id, before);

			try {

				/**
				 * 将数据传到客户端,现在的数据并没有经过客户端的处理
				 */

				asyncContext.getResponse().getWriter().write(statusArray.toString());
			} catch (IOException e) {
				try {
					// 向客户端告知网络IO出错
					asyncContext.getResponse().getWriter().write(ErrorCodeJson.IOERROR.toString());
				} catch (IOException e1) {
					System.err.println("网络错误---------没办法了");
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			// 一次请求完成,释放相关资源
			asyncContext.complete();
		} catch (SQLException e) {
			try {
				// 向客户端告知服务器SQL出错
				asyncContext.getResponse().getWriter().write(ErrorCodeJson.SQLERROR.toString());
			} catch (IOException e1) {
				System.err.println("网络错误---------没办法了!");
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
