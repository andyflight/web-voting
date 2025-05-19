<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Votings</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<jsp:include page="/WEB-INF/views/header.jsp" />

<div class="container">
    <h1 class="page-title">My Votings</h1>
    <c:choose>
        <c:when test="${empty votings}">
            <div class="message message-info">
                <p>You have not created any voting yet.</p>
                <p><a href="${pageContext.request.contextPath}/votings/create" class="btn btn-primary">Create a new Voting</a></p>
            </div>
        </c:when>
        <c:otherwise>
            <ul class="voting-list">
                <c:forEach var="voting" items="${votings}">
                    <li class="voting-item">
                        <div class="card">
                            <div class="card-body">
                                <h2>
                                    <a href="${pageContext.request.contextPath}/votings/${voting.id}" class="voting-link">
                                        <c:out value="${voting.title}" />
                                    </a>
                                </h2>
                                <p>Status: <c:out value="${voting.isActive ? 'Active' : 'Stopped'}" /></p>

                                <div class="status-forms">
                                    <c:if test="${!voting.isActive}">
                                        <form action="${pageContext.request.contextPath}/votings/status" method="post">
                                            <input type="hidden" name="votingId" value="<c:out value="${voting.id}"/>">
                                            <input type="hidden" name="status" value="start">
                                            <button type="submit" class="btn btn-success">Activate</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${voting.isActive}">
                                        <form action="${pageContext.request.contextPath}/votings/status" method="post">
                                            <input type="hidden" name="votingId" value="<c:out value="${voting.id}"/>">
                                            <input type="hidden" name="status" value="stop">
                                            <button type="submit" class="btn btn-warning">Stop</button>
                                        </form>
                                    </c:if>
                                </div>
                                <div class="card-footer" style="margin-top: 10px;">
                                    <a href="${pageContext.request.contextPath}/votings/<c:out value="${voting.id}"/>" class="btn btn-info">View Details</a>
                                    <a href="${pageContext.request.contextPath}/results/<c:out value="${voting.id}"/>" class="btn btn-secondary">View Results</a>
                                </div>
                            </div>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>