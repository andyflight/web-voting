<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Votings List</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<jsp:include page="/WEB-INF/views/header.jsp" />

<div class="container">
    <h1 class="page-title">Available Votings</h1>

    <c:if test="${not empty error}">
        <div class="message message-error">
            <p>Invalid username. Please try again.</p>
        </div>
    </c:if>

    <c:if test="${empty votings}">
        <div class="message message-info">
            <p>No votings available at the moment.</p>
        </div>
    </c:if>

    <c:if test="${not empty votings}">
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
                            <c:if test="${voting.isActive}">
                                <p>Status: <span class="status-active">Active</span></p>
                                <div class="card-footer">
                                    <a href="${pageContext.request.contextPath}/votings/<c:out value="${voting.id}"/>" class="btn btn-primary">Vote Now</a>
                                </div>
                            </c:if>
                            <c:if test="${!voting.isActive}">
                                <p>Status: <span class="status-active">Stopped</span></p>
                                <div class="card-footer">
                                    <a href="${pageContext.request.contextPath}/results/<c:out value="${voting.id}"/>" class="btn btn-secondary">See Results</a>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </c:if>
</div>
</body>
</html>