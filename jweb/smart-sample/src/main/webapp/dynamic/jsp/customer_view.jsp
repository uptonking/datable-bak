<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="base" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=8">
    <title>Smart Smaple - Customer</title>
    <link rel="stylesheet" href="${base}/static/asset/style/global.css"/>
</head>
<body>

<div id="header">
    <div id="logo">Smart Sample</div>
    <div id="oper">
        <button type="button" id="logout">Logout</button>
    </div>
</div>

<div id="content">
    <form id="customer_view_form" class="css-form">
        <input type="hidden" id="id" value="${customer.id}"/>
        <div class="css-form-header">
            <h3>View Customer</h3>
        </div>
        <div class="css-form-row">
            <label for="customer_name">Customer Name:</label>
            <input type="text" id="customer_name" name="customerName" value="${customer.customerName}" class="css-readonly" readonly/>
        </div>
        <div class="css-form-row">
            <label for="description">Description:</label>
            <textarea id="description" name="description" rows="5" class="css-readonly" readonly>${customer.description}</textarea>
        </div>
        <div class="css-form-footer">
            <button type="button" id="edit">Edit</button>
            <button type="button" id="cancel">Cancel</button>
        </div>
    </form>
</div>

<div id="footer">
    <div id="copyright">Copyright @ 2013</div>
</div>

<script type="text/javascript" src="${base}/static/asset/lib/jquery/jquery.min.js"></script>
<script type="text/javascript" src="${base}/static/asset/lib/jquery-form/jquery.form.min.js"></script>
<script type="text/javascript" src="${base}/static/asset/script/global.js"></script>
<script type="text/javascript" src="${base}/static/script/customer_view.js"></script>

</body>
</html>