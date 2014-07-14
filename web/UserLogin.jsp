
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户登录</title>
</head>
<body>
    <form action="LoginServlet" method="post">
        email：<input type="text" name="email"/><br/>
        密码：<input type="password" name="password"/><br/>
        <input type="submit" value="登录"/><br/>
    </form>
</body>
</html>
