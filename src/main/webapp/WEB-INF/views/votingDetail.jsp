<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>Voting Details</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="${pageContext.request.contextPath}/js/scripts.js"></script>
</head>
<body>
<jsp:include page="/WEB-INF/views/header.jsp" />

<div class="container">
    <div class="card">
        <div class="card-header">
            <h1 class="page-title"><c:out value="${voting.title}" /></h1>
        </div>
        <div class="card-body">
            <div class="voting-details">
                <p>Created by: <c:out value="${voting.creator.name}" /></p>
                <p>Total votes: <c:out value="${fn:length(voting.votes)}" /></p>
                
                <c:if test="${voting.isActive}">
                    <div class="message message-info">
                        <p>This voting is currently active and accepting votes.</p>
                    </div>
                </c:if>
                <c:if test="${not voting.isActive}">
                    <div class="message message-warning">
                        <p>This voting is currently closed.</p>
                    </div>
                </c:if>
            </div>
            <div class="share-links">
                <h3>Share this voting:</h3>
                <div class="form-group">
                    <label class="form-label">Voting Link:</label>
                    <div class="input-group">
                        <input type="text" class="form-control" readonly 
                               value="<c:out value="${votingUrl}"/>">
                        <button class="btn btn-primary copy-link-btn" 
                                data-clipboard-text="<c:out value="${votingUrl}"/>">
                            Copy
                        </button>
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">Results Link:</label>
                    <div class="input-group">
                        <input type="text" class="form-control" readonly
                               value="<c:out value="${resultUrl}"/>">
                        <button class="btn btn-primary copy-link-btn"
                                data-clipboard-text="<c:out value="${resultUrl}"/>">
                            Copy
                        </button>
                    </div>
                </div>
            </div>
            <c:choose>
                <c:when test="${voting.isActive}">
                    <div class="voting-form">
                        <h3>Cast Your Vote</h3>
                        <c:if test="${not empty error}">
                            <div class="message message-error">
                                <c:out value="${error}" />
                            </div>
                        </c:if>
                        <c:if test="${not empty message}">
                            <div class="message message-info">
                                <c:out value="${message}" />
                            </div>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/votings/vote" method="post">
                            <input type="hidden" name="votingId" value="<c:out value="${voting.id}"/>">
                            
                            <div class="form-group">
                                <label class="form-label">Select a candidate:</label>
                                <c:forEach var="candidate" items="${voting.candidates}">
                                    <div class="radio-option">
                                        <input type="radio" id="candidate-<c:out value="${candidate.id}"/>" 
                                               name="candidateId" value="<c:out value="${candidate.id}"/>" required>
                                        <label for="candidate-${candidate.id}">
                                            <c:out value="${candidate.name}" />
                                        </label>
                                    </div>
                                </c:forEach>
                            </div>

                            <button type="submit" class="btn btn-primary">Submit Vote</button>
                        </form>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="results-preview">
                        <h3>Voting Results</h3>
                        <c:if test="${empty voting.votes}">
                            <div class="message message-info">
                                <p>No votes have been cast yet.</p>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty voting.votes}">
                            <ul class="results-list">
                                <c:forEach var="candidate" items="${voting.candidates}">
                                    <c:set var="voteCount" value="0" />
                                    <c:forEach var="vote" items="${voting.votes}">
                                        <c:if test="${vote.candidate.id eq candidate.id}">
                                            <c:set var="voteCount" value="${voteCount + 1}" />
                                        </c:if>
                                    </c:forEach>
                                    
                                    <li>
                                        <span class="candidate-name"><c:out value="${candidate.name}" /></span>
                                        <span class="vote-count"><c:out value="${voteCount}"/> vote<c:if test="${voteCount != 1}">s</c:if></span>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:if>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="card-footer">
            <a href="${pageContext.request.contextPath}/results/<c:out value="${voting.id}"/>" class="btn btn-secondary">See Results</a>
        </div>

        <div class="card-footer">
            <a href="${pageContext.request.contextPath}/votings" class="btn btn-primary">Back to Votings</a>
        </div>
    </div>
</div>
</body>

</html>