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

	<link rel="stylesheet" type="text/css" href="css/index/idexpage.css"/>
	<link rel="stylesheet" type="text/css" href="css/index/login.css"/>
	<script type="text/javascript">

		function login() {
			// 显示隐藏的div
			var div = document.getElementById("login_background");

			div.setAttribute("style","display:block");

			var login_page = document.getElementById("real_login");

			login_page.setAttribute("style","display:block");
		}
	</script>
</head>
<body>
<div id="login_background" style="display: none">
</div>
<div id="real_login" style="display:none">
	<iframe id="login_iframe_page" src="html/index/login.html" width="400px" height="330px" scrolling="no">

	</iframe>
</div>



<img class="front-image" src="source/img/exp_wc2014_gen_laurenlemon.jpg" style="position:absolute; left: 0;top: 0;right: 0;bottom: 0">

<div id="main" style="position:absolute;top: 0px">


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
			<button class="top_button" value="登陆" onclick="login()">登陆</button>
		</div>


	</div>

	<!-- 这是中间内容部分 -->
	<div id="middle">

		<iframe id="iframe_page" src="https://localhost:8443/html/index/register.html" width="450px" height="400px"
		        frameborder="0"
		        scrolling="NO">

		</iframe>
	</div>

</div>

<%--<!-- 下面的官话和相关事宜 -->--%>
<%--<div id="bottom">--%>
<%--<hr width="80%" /> --%>
<%--</div>--%>

</div>

</body>
</html>
