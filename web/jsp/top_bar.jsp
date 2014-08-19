<%--
  Created by IntelliJ IDEA.
  User: leon
  Date: 2014/8/2
  Time: 16:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--顶部的菜单栏--%>
<div id="top_bar">
	<div id="top_container">
		<div id="home_page">
			<%--前面或者后面需要加一个home的图片来装饰--%>
			<a href="../main.jsp">瞬  间</a>
		</div>

		<div id="all_search">
			<input type="text" placeholder="搜索话题、朋友或关键字...">
		</div>

		<div id="right_operate_container">
			<%--这里是试试手气、个人主页、设置的存放位置--%>

			<ul>
				<li><a href="../main.jsp">试试手气</a></li>

				<li><a href="../main.jsp">个人主页</a></li>

				<li><a href="../main.jsp">设置</a></li>
			</ul>

		</div>
	</div>
</div>
