<%--
  Created by IntelliJ IDEA.
  User: leon
  Date: 2014/7/24
  Time: 15:17
  To change this template use File | Settings | File Templates.

  这个页面是主页，是登陆和注册后跳转到此的页面，是查看朋友动态的页面

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="css/main/main_page.css">
	<link rel="stylesheet" type="text/css" href="css/main/left_content.css">
	<link rel="stylesheet" type="text/css" href="css/main/middle_content.css">
	<link rel="stylesheet" type="text/css" href="css/main/top_bar.css">
	<title>瞬间</title>
</head>
<body>

	<%--导入top_bar--%>
	<c:import url="jsp/top_bar.jsp">
	</c:import>

	<%--背景图片--%>
	<img id="background_img" src="source/img/main_page_bg.jpg">

	<%--中间内容部分--%>
	<div id="main">

		<%--左边的个人摘要和热门推荐div--%>
		<div id="left_content">

			<div id="self_content">
				<a href="main.jsp">
					<img class="left_img" id="left_bg_img" src="source/img/bg_img.jpg" width="290px" height="100px">
				</a>

				<div id="pic">
					<a class="self_main_page" href="main.jsp">
						<img class="left_img" src="source/img/me.jpeg" width="70px" height="70px">
					</a>
				</div>

				<div id="nick_name_container">
					<a id="nick_name" href="main.jsp">${sessionScope.name}</a>
				</div>

				<div id="function_area">
					<hr/>
					<table border="1">
						<tr>
							<td><a href="main.jsp">我的圈子</a></td>
							<td><a href="main.jsp">通知中心</a></td>
						</tr>
					</table>
				</div>

			</div>

			<div id="hot_container">
				<div id="hot_top">
					<p style="display: inline;font-size: 20px;margin-left: 10px">热门图片</p>
					<a href="main.jsp" style="font-size: 12px">设置</a>
				</div>

				<div id="hot_pic">
					<a href="main.jsp">
						<img src="source/img/exp_wc2014_gen_laurenlemon.jpg" width="290px" height="217.5px">
					</a>

					<button type="button">喜欢</button>
					<button type="button">转发</button>

					<input type="text" placeholder="说点什么？">
				</div>
			</div>




		</div>

		<%--中间的消息动态区div--%>
		<div id="middle_content">

			<div id="class_switcher">
				<ul>
					<li><a href="main.jsp">全部</a></li>
					<li><a href="main.jsp">朋友</a></li>
					<li><a href="main.jsp">热门</a></li>
					<li><a href="main.jsp">关注的人</a></li>
				</ul>
			</div>

			<div id="content">

				<ul>
					<%--这是一个动态的显示--%>
					<li>
						<div class="content_container">
							<div class="top_content_container">

								<div class="head_pic_container">
									<a href="main.jsp"><img src="source/img/others.png"></a>
								</div>
								<div class="nick_name_container">
									<a href="main.jsp">瞬间</a>
								</div>

								<div class="time_location">
									<%--这里前面以后美化的时候打算加一个时钟的小图片--%>
									<p>13:25</p>
									<%--这里以后美化的时候打算加一个能表示地点的小图片--%>
									<p>成都信息工程学院</p>
								</div>

							</div>

							<div class="middle_content_container">
								<img src="source/img/content2.jpg">
							</div>


							<%--显示细节，文字描述、转发的人--%>
							<div class="detail">

							</div>


							<%--这里是显示有多少喜欢、评论、转发的区域--%>
							<%--下面三个全是图片，暂时使用文字代替--%>
							<div class="view_others__container">
								<button><p>细节</p></button>
								<button><p>喜欢</p></button>
								<button><p>转发</p></button>
								<input type="text" placeholder="我也说点什么....">
							</div>




						</div>
					</li>
				</ul>


			</div>


		</div>

		<%--右边的可能认识的人和官方标记div--%>
		<div id="right_content">

		</div>


	</div>

</body>
</html>
