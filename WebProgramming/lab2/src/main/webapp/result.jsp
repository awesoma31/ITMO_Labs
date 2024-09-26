<%@ page import="java.util.ArrayList" %>
<%@ page import="org.awesoma.lab2.models.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Web lab1 Result</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body>
<div class="background-top"></div>
<div class="content">
    <nav class="navbar">
    <div id="info">
        Alexander Churakov P3231, var. 669
    </div>
    <a href="https://github.com/awesoma31" target="_blank" id="github">github</a>
</nav>
    <main class="container">
    <table id="result-table">
        <thead>
            <tr>
                <th>X</th>
                <th>Y</th>
                <th>R</th>
                <th>time</th>
                <th>execution time, ns</th>
                <th>Is Inside</th>
            </tr>
        </thead>
        <tbody>
        <%
            @SuppressWarnings("unchecked")
            ArrayList<Point> results = (ArrayList<Point>) session.getAttribute("results");
            for (Point point : results) {
        %>
            <tr>
                <td><%=point.x()%></td>
                <td><%=point.y()%></td>
                <td><%=point.r()%></td>
                <td><%=point.now().toString()%></td>
                <td><%=point.execTime()%></td>
                <td><%=point.isInside()%></td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>
</main>
    <button id="back-button" type="button" onclick="window.location.href='${pageContext.request.contextPath}/'">Back</button>
</div>
<footer id="copyright">pgLangInspired, 2024</footer>
<div class="background-bot"></div>
</body>
</html>
