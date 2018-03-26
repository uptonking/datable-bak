<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort() + path + "/";
    String resBasePath = basePath + "resources";

    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Excel文件查询主页面</title>
    <link rel="stylesheet" href="<%=resBasePath%>/public/dist/css/light7.min.css">
    <link rel="stylesheet" href="<%=resBasePath%>/public/dist/css/light7-swiper.min.css">
    <link rel="stylesheet" href="<%=resBasePath%>/public/dist/css/index.css">
    <script type='text/javascript' src='<%=resBasePath%>/public/dist/js/jquery-3.2.1.min.js' charset='utf-8'></script>
    <script type='text/javascript' src='<%=resBasePath%>/public/dist/js/light7.min.js' charset='utf-8'></script>
</head>
<body>
<form>
    <br>
    <br>
    <div align="center">
        <input type="hidden" id="projectPath" name="projectPath"
               value="<%=basePath%>"/>
        文件A: <input type="text" id="startTime_A"
                    name="startTime_A" placeholder="起始时间,格式yyyyMMddhhmmss" value="20170810210000"/> 至
        <input type="text" id="endTime_A" name="endTime_A" value="20170812210000" placeholder="终止时间,格式yyyyMMddhhmmss"/><br>
        文件B: <input
            type="text" id="startTime_B" name="startTime_B"
            placeholder="起始时间,格式yyyyMMddhhmmss"> 至 <input type="text"
                                                          id="endTime_B" name="endTime_B" placeholder="终止时间,格式yyyyMMddhhmmss"><br>
        文件AB: <input type="text" id="startTime_AB" name="startTime_AB"
                     placeholder="起始时间,格式yyyyMMddhhmmss"> 至 <input type="text"
                                                                   id="endTime_AB" name="endTime_AB"
                                                                   placeholder="终止时间,格式yyyyMMddhhmmss"><br>

        <input type="button" value="文件A查询" id="buttonA"/>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" value="文件B查询" id="buttonB"/>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" value="文件AB同时查询" id="buttonAB"/>

        <br>
        <br>
        <br> 显示结果<br>
        文件A统计结果: &nbsp;<input type="text"
                              id="sumTwo_A" name="sumTwo_A" placeholder="文件A第二列结果" value=""/>&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="text" id="sumThree_A" name="sumThree_A"
               placeholder="文件A第三列结果" value=""/><br>
        文件B统计结果: &nbsp;&nbsp;<input
            type="text" id="sumTwo_B" name="sumTwo_B" placeholder="文件B第二列结果"
            value=""/> &nbsp;&nbsp;&nbsp;&nbsp; <input type="text"
                                                       id="sumThree_B" name="sumThree_B" placeholder="文件B第三列结果" value=""/><br>
        文件AB统计结果: <input type="text" id="sumTwo_AB" name="sumTwo_AB"
                         placeholder="文件AB第二列结果" value=""/> &nbsp;&nbsp;&nbsp;&nbsp; <input
            type="text" id="sumThree_AB" name="sumThree_AB"
            placeholder="文件AB第三列结果" value=""/><br>
    </div>
</form>
</body>
<script type="text/javascript">
    $(function () {

        $("#buttonA").click(function () {
            var startTime_AVal = $("#startTime_A").val();
            var endTime_AVal = $("#endTime_A").val();
            if (checkDate(startTime_AVal) && checkDate(endTime_AVal)) {
                query(startTime_AVal, endTime_AVal, "a");
            }
        });

        $("#buttonB").click(function () {
            var startTime_BVal = $("#startTime_B").val();
            var endTime_BVal = $("#endTime_B").val();
            if (checkDate(startTime_BVal) && checkDate(endTime_BVal)) {
                query(startTime_BVal, endTime_BVal, "b");
            }
        });

        $("#buttonAB").click(function () {
            var startTime_ABVal = $("#startTime_AB").val();
            var endTime_ABVal = $("#endTime_AB").val();
            if (checkDate(startTime_ABVal) && checkDate(endTime_ABVal)) {
                query(startTime_ABVal, endTime_ABVal, "ab");
            }
        });
    });

    function checkDate(dateval) {
        if ('undefined' == typeof (dateval) || "" == dateval || null == dateval) {
            alert('请输入时间');
            return false;
        } else {
            if (14 != dateval.length) {
                alert('请输入正确的时间,格式为:yyyyMMddhhmmss');
                return false;
            } else {
                return true;
            }
        }
    }

    //querySign:a-查询a,b-查询b,ab-查询AB
    function query(startTime, endTime, querySign) {
        var path_Pro = $("#projectPath").val() + 'query01?startTime=' + startTime + "&endTime=" + endTime + "&querySign=" + querySign;
        $.ajax({
            type: "GET",
            url: path_Pro,
            // dataType: "json",
            success: function (data) {
                //[{'sumColTwo':'1','sumColThree':'2'},{'sumColTwo':'3','sumColThree':'4'},{'sumColTwo':'4','sumColThree':'6'}]
                console.log('success');
                // console.log(data);
                // console.log(typeof data);
                data = "{\'arr\':" +data+ "}";
                data=data.replace(/\'/g,"\"")
                // console.log(data)
                data = JSON.parse(data).arr;

                $("#sumTwo_A").val(data[0]["sumColTwo"]);
                $("#sumThree_A").val(data[0]['sumColThree']);

                $("#sumTwo_B").val(data[1]['sumColTwo']);
                $("#sumThree_B").val(data[0]['sumColThree']);

                $("#sumTwo_AB").val(data[2]['sumColTwo']);
                $("#sumThree_AB").val(data[0]['sumColThree']);
            },
            error: function (response) {
                console.log('failed');
                console.log(response);
            }
        });
    }
</script>
</html>
