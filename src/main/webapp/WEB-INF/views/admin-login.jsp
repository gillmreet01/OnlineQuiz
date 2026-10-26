<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Admin Login" />
<%@ include file="fragments/header.jspf" %>

<div class="container" style="max-width:440px">
  <div class="card">
    <h2>Admin Login &#128274;</h2>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}" /></div></c:if>
    <c:if test="${not empty info}"><div class="alert info"><c:out value="${info}" /></div></c:if>

    <form method="post" action="${ctx}/adminlogin">
      <label class="field">Admin username
        <input type="text" name="username" value="<c:out value='${param.username}' />" required autofocus>
      </label>
      <label class="field">Password
        <input type="password" name="password" required>
      </label>
      <button class="btn block" type="submit">Login as Admin</button>
    </form>
  </div>
</div>

<%@ include file="fragments/footer.jspf" %>
