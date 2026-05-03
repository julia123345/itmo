<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Area Checker</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/script.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
<div class="container">
    <header>
        <h1>Area Checker Application</h1>
        <p>Student: Новикова Юлия | Group: P3208 | Variant: 409255</p>
    </header>

    <form id="mainForm" action = "control" method="POST">
        <input type="hidden" id="rValue" name="r" value="${sessionScope.selectedR}">
    </form>

    <div class="form-section">
        <h3>Select R Value:</h3>
        <div class="r-selector">
            <c:forEach var="i" begin="1" end="5">
                <div class="r-option ${i eq sessionScope.selectedR ? 'selected' : ''}"
                     data-value="${i}" onclick="selectR(${i})">${i}</div>
            </c:forEach>
        </div>
        <p>Current R: <span id="currentR">${sessionScope.selectedR != null ? sessionScope.selectedR : 'not selected'}</span></p>
    </div>

    <div class="canvas-section">
        <canvas id="areaCanvas" width="500" height="500"></canvas>
        <p>Click on the canvas to select coordinates</p>
    </div>

    <div class="results-section">
        <h3>Results History:</h3>
        <c:choose>
            <c:when test="${not empty sessionScope.results}">
                <table id="resultsTable">
                    <thead>
                    <tr>
                        <th>X</th><th>Y</th><th>R</th><th>Result</th><th>Time</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="result" items="${sessionScope.results}">
                        <tr>
                            <td>${result.x}</td>
                            <td>${result.y}</td>
                            <td>${result.r}</td>
                            <td class="${result.hit ? 'hit' : 'miss'}">${result.hit ? 'HIT' : 'MISS'}</td>
                            <td>${result.time}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>No results yet</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    const initialR = Number("${sessionScope.selectedR != null ? sessionScope.selectedR : 0}");
</script>
</body>
</html>