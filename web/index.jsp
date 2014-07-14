<%--
  Created by IntelliJ IDEA.
  User: leon
  Date: 2014/7/9
  Time: 4:43
  To change this template use File | Settings | File Templates.

  这个页面是登陆注册页面

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>瞬间</title>

	<link rel="stylesheet" type="text/css" href="css/index/idexpage.css">
  </head>
  <body>
	<div id="main">


		<!-- 顶部“瞬间”宣传语和登陆、试试手气-->
		<div id="top">
			<div id="name">
			<%--这里以后会放上一张图片--%>
				<a href="www.baidu.com">瞬间</a>
			</div>

			<div id="word">
				<p>感受生活，分享瞬间</p>
			</div>

			<div id="try_luck">
					<button class="top_button" value="试试手气">试试手气</button>
			</div>

			<div id="login_button">
				<button class="top_button" value="登陆">登陆</button>
			</div>



		</div>

		<!-- 这是中间内容部分 -->
		<div id="middle">


			<div id="video">
				<p>你能干什么？</p>
				<video src="source/video/index.mp4" width="400px" height="300px" controls="controls"></video>
			</div>

			<div id="sign_up">
				<div id="nick_name">
					<input type="text">
				</div>

				<div id="email_input">

					<input itype="email">
				</div>


				<input id="password" type="password">
				<input id="password_again" type="password">
				<button id="sign" type="button">注册</button>
			</div>


		</div>

		<!-- 下面的官话和相关事宜 -->
		<div id="bottom">

		</div>

	</div>

  </body>
</html>
