package model.status;

import dao.status.StatusDao;
import dao.status.StatusLikeDao;
import dao.user.UserInfoDao;
import model.notify.NotifyCacheManager;
import utils.EnumUtil.ErrorCode;
import utils.EnumUtil.NotifyType;
import utils.StatusResponseHandler;
import utils.json.JSONObject;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * author：康乐
 * time：2014/11/19
 * function：管理状态点赞相关
 */

public class StatusLikeHandler implements Runnable {

	// 点赞或取消赞的人的id
	private int liker;

	private int likeder;

	// 被操作的资源的标记
	private int rs_id;

	// 是点赞 还是 取消
	private boolean isLike;

	private AsyncContext asyncContext;

	/**
	 * 管理类的构造方法
	 *
	 * @param liker 喜欢的人的ID
	 * @param rs_id 被喜欢资源的rs_id
	 */
	public StatusLikeHandler(int liker, int likeder, int rs_id, boolean isLike, AsyncContext asyncContext) {
		this.liker = liker;
		this.likeder = likeder;
		this.rs_id = rs_id;
		this.isLike = isLike;
		this.asyncContext = asyncContext;
	}


	/**
	 * 现在缓存中进行操作，如果缓存的操作返回为false，表示并未进行操作；
	 * 再通过调用数据库DAO进行操作；如果抛出sql异常，则返回给用户后台
	 * sql操作出现问题
	 * <p/>
	 * 另外，dao层如果有返回，只有可能返回true；否则则是抛出异常
	 */
	@Override
	public void run() {

		boolean isDone = false;
		try {
			// 如果是点赞
			if (isLike) {
				// 首先检查此人是否已经赞过此状态，由于没有点赞的情况会有两种
				// 1.确实没有点赞，但存在此状态    2.不存在此状态
				if (StatusLikeDao.isLiked(rs_id, liker)) {
					// 如果赞过则返回
					StatusResponseHandler.sendStatus("status", ErrorCode.FAIL,
							(HttpServletResponse) asyncContext.getResponse());
					return;
				}
				// 如果此人对此状态没有点过赞，则首先在缓存中进行操作
				isDone = StatusRowSetManager.likeIt(rs_id);
				// 如果在缓存中成功记录，便在数据库中记录相关的用户信息
				if (isDone) {
					// 如果记录成功
					if (StatusLikeDao.recordLiker(rs_id, liker))
						addMessage();
						asyncContext.complete();

				} else {
					// 如果在缓存中的操作返回false，则表明缓存中不存在此状态。那么在
					// 数据库操作时，首先检查是否存在该状态
					if (!StatusDao.isExisted(rs_id)) {
						StatusResponseHandler.sendStatus("status", ErrorCode.FAIL,
								(HttpServletResponse) asyncContext.getResponse());
						return;
					}
					// 在数据库中直接进行操作
					if (StatusLikeDao.likeIt(rs_id, liker)) {
						addMessage();
						asyncContext.complete();
					}

				}
			} else {
				// 如果是取消点赞，首先检查此人是否已经对此状态点过赞
				// 如果点过，才可以取消；否则返回false
				if (StatusLikeDao.isLiked(rs_id, liker)) {
					// 先在缓存中进行减点赞数操作
					isDone = StatusRowSetManager.unLikeIt(rs_id);
					// 如果在缓存中成功记录，便在数据库中删除相关的用户信息
					if (isDone) {
						if (StatusLikeDao.removeLiker(rs_id, liker))
							asyncContext.complete();

					} else {
						// 如果在缓存中的操作返回false，则表明缓存中不存在此状态。那么在
						// 数据库操作时，首先检查是否存在该状态
						if (!StatusDao.isExisted(rs_id)) {
							StatusResponseHandler.sendStatus("status", ErrorCode.FAIL,
									(HttpServletResponse) asyncContext.getResponse());
							return;
						}
						if (StatusLikeDao.unLikeIt(rs_id, liker))
							asyncContext.complete();
					}
				} else {
					StatusResponseHandler.sendStatus("status", ErrorCode.FAIL,
							(HttpServletResponse) asyncContext.getResponse());
				}
			}
		} catch (SQLException e) {
			// 这里回溯对缓存或者数据库的修改
			// 如果isDone是真，则表示在缓存中进行了修改，那么需要回溯缓存中的修改
			if (isDone) {
				try {
					if (isLike) {
						StatusRowSetManager.unLikeIt(rs_id);
					} else {
						StatusRowSetManager.likeIt(rs_id);
					}
				} catch (SQLException e1) {
					// 如果回溯也出现问题，那么问题无法解决
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
			StatusResponseHandler.sendStatus("status", ErrorCode.SQLERROR,
					(HttpServletResponse) asyncContext.getResponse());
		}
	}

	private void addMessage() throws SQLException {
		/**
		 * 将消息放入推送map
		 */
		String name = UserInfoDao.getUserName(liker);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("content",name + "赞了你的照片");
		jsonObject.put("class", NotifyType.LIKE);
		NotifyCacheManager.putMessage(likeder, jsonObject.toString());
	}
}
