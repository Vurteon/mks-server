
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录消息</title>
</head>
<body>
    <%
            String loginMessage = (String)session.getAttribute("loginMessage");
    %>
            ${loginMessage}
</body>
</html>
