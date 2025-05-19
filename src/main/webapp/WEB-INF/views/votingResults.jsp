<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Voting Results - <c:out value="${voting.title}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<jsp:include page="/WEB-INF/views/header.jsp"/>

<div class="container">
    <h1 class="page-title">Results for: <c:out value="${voting.title}"/></h1>

    <c:if test="${empty results}">
        <div class="message message-info">
            <p>No results available for this voting yet, or the voting had no candidates.</p>
        </div>
    </c:if>

    <c:if test="${not empty results}">
        <div class="card">
            <div class="card-body">
                <ul class="results-list">
                    <c:forEach var="entry" items="${results}">
                        <li>
                            <span class="candidate-name"><c:out value="${entry.key}"/></span>
                            <span class="vote-count"><c:out value="${entry.value}"/> votes</span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </c:if>
    <br/>
    <a href="${pageContext.request.contextPath}/votings/<c:out value="${voting.id}"/>" class="btn">Back to Voting Details</a>
    <a href="${pageContext.request.contextPath}/votings" class="btn btn-secondary">Back to Votings List</a>
</div>
</body>
</html>