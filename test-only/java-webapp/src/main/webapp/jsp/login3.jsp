<%@ page language="java" isELIgnored="false" pageEncoding="UTF-8" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>login3登录页面</title>
    <%--<script type="text/javascript" src="${pageContext.request.contextPath}/ajaxUtil.js"></script>--%>
    <%--<script type="text/javascript" src="${pageContext.request.contextPath}/js/Utils.js"></script>--%>
</head>

<body>
<fieldset>
    <legend>用户登录</legend>
    <form>
        用户名：<input type="text" name="usename" id="usename">
        <br/>
        密码：<input type="text" name="pwd" id="pwd">
        <br/>
        <input type="button" value="登录" onclick="login()"/>
    </form>
</fieldset>

<script type="text/javascript">

    function login() {
        Ajax.request({
            url: "${pageContext.request.contextPath}/ajaxLogin/handle.do",
            data: {
                "usename": document.getElementById("usename").value,
                "pwd": document.getElementById("pwd").value
            },
            success: function (xhr) {
                onData(xhr.responseText);
            },
            error: function (xhr) {

            }
        });
    }

    function onData(responseText) {
        if (responseText == "success") {
            //window.location.href="index.jsp";//改变url地址
            /*
            window.location.replace("url")：将地址替换成新url，
            该方法通过指定URL替换当前缓存在历史里（客户端）的项目，因此当使用replace方法之后，
            你不能通过“前进”和“后 退”来访问已经被替换的URL，这个特点对于做一些过渡页面非常有用！
            */
            location.replace(g_basePath + "/index.jsp");
        } else {
            alert("用户名和密码错误");
        }
    }

    //立即执行的js
    (function () {
        //获取contextPath
        var contextPath = getContextPath();
        //获取basePath
        var basePath = getBasePath();
        //将获取到contextPath和basePath分别赋值给window对象的g_contextPath属性和g_basePath属性
        window.g_contextPath = contextPath;
        window.g_basePath = basePath;
    })();

    /**
     * @author 孤傲苍狼
     * 获得项目根路径，等价于jsp页面中
     String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
     * 使用方法：getBasePath();
     * @returns 项目的根路径
     *
     */
    function getBasePath() {
        var curWwwPath = window.document.location.href;
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        var localhostPath = curWwwPath.substring(0, pos);
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        return (localhostPath + projectName);
    }

    /**
     * @author 孤傲苍狼
     * 获取Web应用的contextPath，等价于jsp页面中
     *  <%
        String path = request.getContextPath();
    %>
     * 使用方法:getContextPath();
     * @returns /项目名称(/EasyUIStudy_20141104)
     */
    function getContextPath() {
        return window.document.location.pathname.substring(0, window.document.location.pathname.indexOf('\/', 1));
    };
</script>

</body>
</html>
