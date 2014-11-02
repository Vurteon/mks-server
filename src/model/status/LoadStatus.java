package model.status;

import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;
import utils.json.JSONArray;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
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
			// 从缓存或者数据库中获取数据，有可能存在数据
			JSONArray statusArray = StatusRowSetManager.selectStatus(followings, rs_id, before);

			// 向客户端发送数据
			StatusResponseHandler.sendStatus(statusArray, (HttpServletResponse) asyncContext.getResponse(),false);
			asyncContext.complete();
		} catch (SQLException e) {
			// 向客户端告知IO出错
			StatusResponseHandler.sendStatus("status", ErrorCode.SQLERROR,
					(HttpServletResponse) asyncContext.getResponse());
		}
	}
}
