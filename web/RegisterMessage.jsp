
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>注册消息</title>
</head>
<body>
        <%
            String registerMessage = (String)session.getAttribute("registerMessage");
        %>
        ${registerMessage}
</body>
</html>
