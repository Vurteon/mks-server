package model.status;

import dao.status.StatusCommentDao;
import dao.status.StatusDao;
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
 * time：2014/11/20
 * function：管理评论相关model
 */

public class StatusCommentHandler implements Runnable {

	private int rs_id;

	private int commenter;

	private int commented;

	private String content;

	private AsyncContext asyncContext;
	/**
	 * 异步处理评论
	 *
	 * @param rs_id     被评论资源的id
	 * @param commenter 评论者
	 * @param commented 被评论者
	 * @param content   内容
	 */
	public StatusCommentHandler(int rs_id, int commenter, int commented, String content, AsyncContext asyncContext) {
		this.rs_id = rs_id;
		this.commenter = commenter;
		this.commented = commented;
		this.content = content;
		this.asyncContext = asyncContext;
	}

	/**
	 * 现在缓存中进行评论+1的操作，如果成功，则在数据库进行记录操作；如果
	 * 返回false，则表示缓存中不存在此条状态，需要在数据库做所有的工作
	 */
	@Override
	public void run() {

		boolean isAdd = false;

		try {
			if (isAdd = StatusRowSetManager.addCommentNumber(rs_id)) {
				// 如果在缓存中评论成功
				if (StatusCommentDao.recordComment(rs_id, commenter, commented, content)) {
					// 推送消息
					addMessage();
					asyncContext.complete();
				}
			}else {
				// 如果在缓存中不存在，则在数据库中进行操作
				if (StatusDao.isExisted(rs_id)) {
					// 如果此状态存在
					if (StatusCommentDao.comment(rs_id, commenter, commented, content)) {
						// 推送消息
						addMessage();
						asyncContext.complete();
					}
				}else {
					// 如果不存在，返回给客户端错误信息
					StatusResponseHandler.sendStatus("status", ErrorCode.FAIL,
							(HttpServletResponse) asyncContext.getResponse());
				}
			}
		} catch (SQLException e) {
			if (isAdd) {
				try {
					StatusRowSetManager.subCommentNumber(rs_id);
				} catch (SQLException e1) {
					e1.printStackTrace();
					System.err.println("----------------回滚cache数据错误--------------");
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
		String name = UserInfoDao.getUserName(commenter);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("content",name + "评论：" + content);
		jsonObject.put("class", NotifyType.COMMENT);
		NotifyCacheManager.putMessage(commented, jsonObject.toString());
	}
}
