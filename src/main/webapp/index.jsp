<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Welcome" />
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<div class="container">
  <div class="hero">
    <h1>Test your knowledge &#127891;</h1>
    <p>Pick a level, beat the 10-second timer and see how well you score.</p>
    <div class="actions">
      <c:choose>
        <c:when test="${empty sessionScope.user}">
          <a class="btn" href="${ctx}/login">Student Login</a>
          <a class="btn secondary" href="${ctx}/register">Create Account</a>
          <a class="btn secondary" href="${ctx}/adminlogin">Admin Login</a>
        </c:when>
        <c:when test="${sessionScope.user.admin}">
          <a class="btn" href="${ctx}/admin/dashboard">Go to Dashboard</a>
        </c:when>
        <c:otherwise>
          <a class="btn" href="${ctx}/levels">Start a Quiz</a>
          <a class="btn secondary" href="${ctx}/myresults">My Results</a>
        </c:otherwise>
      </c:choose>
    </div>
  </div>

  <div class="level-grid" style="margin-top:36px">
    <div class="level-card easy"><h3>Easy</h3><p>Warm up with the basics</p></div>
    <div class="level-card medium"><h3>Medium</h3><p>Servlets, JSP and XML</p></div>
    <div class="level-card hard"><h3>Hard</h3><p>For the web-tech experts</p></div>
  </div>
</div>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
