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
    <div id="main">
        <div class="css-panel">
            <div class="css-panel-header">
                <div class="css-left">
                    <h3>Customer List</h3>
                </div>
                <div class="css-right">
                    <a href="${base}/customer_create">New Customer</a>
                </div>
            </div>
            <div class="css-panel-content">
                <div class="css-row">
                    <div class="css-left">
                        <form id="customer_search_form">
                            <div class="css-search">
                                <input type="text" id="customer_name" placeholder="Customer Name"/>
                                <span class="css-search-button">
                                    <button type="submit">Search</button>
                                </span>
                            </div>
                        </form>
                    </div>
                    <div class="css-right">
                        <div id="customer_pager"></div>
                    </div>
                </div>
                <table id="customer_table" class="css-table">
                    <thead>
                    <tr>
                        <td>Customer Name</td>
                        <td>Description</td>
                        <td class="css-width-75">Action</td>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="customer" items="${customerList}">
                        <tr data-id="${customer.id}" data-name="${customer.customerName}">
                            <td>
                                <a href="${base}/customer/view/${customer.id}">${customer.customerName}</a>
                            </td>
                            <td>
                                    ${customer.description}
                            </td>
                            <td>
                                <a href="${base}/customer/edit/${customer.id}">Edit</a>
                                <a href="#" class="ext-customer-delete">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<div id="footer">
    <div id="copyright">Copyright @ 2013</div>
</div>

<script type="text/javascript" src="${base}/static/asset/lib/jquery/jquery.min.js"></script>
<script type="text/javascript" src="${base}/static/asset/lib/jquery-form/jquery.form.min.js"></script>
<script type="text/javascript" src="${base}/static/asset/script/global.js"></script>
<script type="text/javascript" src="${base}/static/script/customer.js"></script>

</body>
</html>