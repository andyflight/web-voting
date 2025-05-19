<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create New Voting</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="${pageContext.request.contextPath}/js/scripts.js"></script>
</head>
<body>
<jsp:include page="/WEB-INF/views/header.jsp" />

<div class="container">
    <div class="card">
        <div class="card-body">
            <c:if test="${not empty error}">
                <div class="message message-error">
                    <p>Invalid input. Please try again.</p>
                </div>
            </c:if>
            <h1 class="page-title">Create New Voting</h1>
            <form action="${pageContext.request.contextPath}/votings/create" method="post">
                <div class="form-group">
                    <label for="title" class="form-label">Voting Title:</label>
                    <input type="text" id="title" name="title" class="form-control" required>
                </div>

                <div class="form-group">
                    <label class="form-label">Candidates:</label>
                    <div id="candidates-container">
                        <div class="candidate-field">
                            <input type="text" name="candidates" class="form-control" placeholder="Candidate name" required>
                        </div>
                    </div>
                    <button type="button" id="add-candidate-btn" class="btn btn-secondary">Add Candidate</button>
                </div>

                <button type="submit" class="btn btn-primary">Create Voting</button>
            </form>
        </div>
    </div>
</div>

</body>
</html>