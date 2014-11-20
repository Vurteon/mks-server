package model.status;

import dao.status.StatusLikeDao;
import utils.EnumUtil.ErrorCode;
import utils.StatusResponseHandler;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/19
 * function：管理状态点赞相关
 */

public class StatusLikeHandler implements Runnable{

	// 点赞或取消赞的人的id
	private int liker;

	// 被操作的资源的标记
	private int rs_id;

	// 是点赞 还是 取消
	private boolean isLike;

	private AsyncContext asyncContext;
	/**
	 * 管理类的构造方法
	 * @param liker 喜欢的人的ID
	 * @param rs_id 被喜欢资源的rs_id
	 */
	public StatusLikeHandler (int liker, int rs_id, boolean isLike, AsyncContext asyncContext) {
		this.liker = liker;
		this.rs_id = rs_id;
		this.isLike = isLike;
		this.asyncContext = asyncContext;
	}


	/**
	 * 现在缓存中进行操作，如果缓存的操作返回为false，表示并未进行操作；
	 * 再通过调用数据库DAO进行操作；如果抛出sql异常，则返回给用户后台
	 * sql操作出现问题
	 */
	@Override
	public void run() {

		boolean isDone = false;
		try {

			// 首先检查此人是否已经赞过此状态
			if (StatusLikeDao.isLiked(rs_id, liker)) {
				asyncContext.complete();
				return ;
			}

			// 如果没有赞过，在缓存中进行操作
			if (isLike) {
				isDone = StatusRowSetManager.likeIt(rs_id);
				// 如果在缓存中成功记录，便在数据库中记录相关的用户信息
				if (isDone) {
					// 如果记录成功
					if (StatusLikeDao.recordLiker(rs_id, liker)) {
						asyncContext.complete();
					}
				}else {
					if (StatusLikeDao.likeIt(rs_id, liker) ) {
						asyncContext.complete();
					}else {
						StatusResponseHandler.sendStatus("status",ErrorCode.FAIL,
								(HttpServletResponse)asyncContext.getResponse());
					}
				}
			}
		} catch (SQLException e) {
			// 这里回溯对缓存或者数据库的修改
			// 如果isDone是真，则表示在缓存中进行了修改，那么需要回溯缓存中的修改
			if (isDone) {
				try {
					StatusRowSetManager.unLikeIt(rs_id);
				} catch (SQLException e1) {
					// 如果回溯也出现问题，那么问题无法解决
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
			StatusResponseHandler.sendStatus("status",ErrorCode.SQLERROR,
					(HttpServletResponse)asyncContext.getResponse());
		}
	}
}
