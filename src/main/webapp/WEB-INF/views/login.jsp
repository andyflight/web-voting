<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <title>Login</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<jsp:include page="/WEB-INF/views/header.jsp" />

<div class="container">
  <div class="login-container card">
    <div class="card-body">
      <h1 class="page-title">Login</h1>
      <c:if test="${not empty error}">
        <div class="message message-error">
          <p>Invalid username. Please try again.</p>
        </div>
      </c:if>
      <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
          <label for="username" class="form-label">Username:</label>
          <input type="text" id="username" name="username" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-primary">Login</button>
      </form>
    </div>
  </div>
</div>
</body>
</html>