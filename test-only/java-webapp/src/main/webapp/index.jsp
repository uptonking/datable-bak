<%@ page language="java" isELIgnored="false" import="java.util.*" pageEncoding="UTF-8"  %>

<!DOCTYPE HTML>
<html>
<head>
    <title>首页</title>
</head>

<body>
当前登录的用户为：
<%--${pageContext.request.usename}--%>
${param.usename}
<%--${paramValues}--%>
<%--<%= request.getParameter("usename")%>--%>
<hr />
<%--${pageContext.request.method}--%>
<br/>
${msg}

</body>
</html>
