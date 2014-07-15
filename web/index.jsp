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
	<script src="js/index/indexscript.js"></script>
  </head>
  <body>
	<div id="main">


		<!-- 顶部“瞬间”宣传语和登陆、试试手气-->
		<div id="top">
			<div id="name">
			<%--这里以后会放上一张图片--%>
				<a href="http://localhost:8080">瞬间</a>
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


			<div id="sign_up">
				<div id="nick_name">
					<input id="nick_name_value" type="text" placeholder="昵称">
				</div>

				<div id="email_input">

					<input type="email" id="email_value" placeholder="邮箱">
				</div>


				<div id="password">
					<input  type="password" id="password_value" placeholder="密码">
				</div>


				<div id="password_again">
					<input type="password" id="password_value_again" placeholder="确认密码">
				</div>

				<div id="sign">
					<button class="sign_button" type="button" onclick="sign_up()">注册</button>
				</div>


			</div>


		</div>

		<%--<!-- 下面的官话和相关事宜 -->--%>
		<%--<div id="bottom">--%>
			<%--<hr width="80%" /> --%>
		<%--</div>--%>

	</div>

  </body>
</html>
