<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header>
  <div class="header-container">
    <nav>
      <a href="${pageContext.request.contextPath}/votings" >Votings List</a>
      <c:if test="${not empty sessionScope.userId}">
        <a href="${pageContext.request.contextPath}/votings/my" >My Votings</a>
        <a href="${pageContext.request.contextPath}/votings/create" >Create Voting</a>
      </c:if>
    </nav>
    <div class="user-actions">
      <c:choose>
        <c:when test="${not empty sessionScope.userId}">
          <form action="${pageContext.request.contextPath}/logout" method="post">
            <button class="btn btn-header-logout" type="submit">Logout</button>
          </form>
        </c:when>
        <c:otherwise>
          <a href="${pageContext.request.contextPath}/login" class="btn btn-header-login">Login</a>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</header>