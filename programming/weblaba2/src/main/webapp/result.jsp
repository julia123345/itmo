<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Area Checker</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Area Check Result</h1>
            <p>Student: Новикова Юлия | Group: P3208 | Variant: 409255</p>
        </header>

        <h2>Last Check</h2>
        <c:choose>
            <c:when test="${not empty sessionScope.results}">
                <table>
                    <tr><th>X</th><th>Y</th><th>R</th><th>Result</th><th>Time</th></tr>
                    <c:set var="lastResult" value="${sessionScope.results[sessionScope.results.size() - 1]}" />
                    <tr>
                        <td>${lastResult.x}</td>
                        <td>${lastResult.y}</td>
                        <td>${lastResult.r}</td>
                        <td class="${lastResult.hit ? 'hit' : 'miss'}">
                            <c:out value="${lastResult.hit ? 'HIT' : 'MISS'}"/>
                        </td>
                        <td>${lastResult.time}</td>
                    </tr>
                </table>
            </c:when>
            <c:otherwise>
                <p>No results available</p>
            </c:otherwise>
        </c:choose>

        <p><a href="${pageContext.request.contextPath}/index.jsp">Back to form</a></p>

        <h3>All Results (session)</h3>
        <c:choose>
            <c:when test="${not empty sessionScope.results}">
                <table>
                    <tr><th>X</th><th>Y</th><th>R</th><th>Result</th><th>Time</th></tr>
                    <c:forEach var="res" items="${sessionScope.results}">
                        <tr>
                            <td>${res.x}</td>
                            <td>${res.y}</td>
                            <td>${res.r}</td>
                            <td class="${res.hit ? 'hit' : 'miss'}">
                                <c:out value="${res.hit ? 'HIT' : 'MISS'}"/>
                            </td>
                            <td>${res.time}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:when>
            <c:otherwise>
                <p>No results yet</p>
            </c:otherwise>
        </c:choose>
    </div>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>