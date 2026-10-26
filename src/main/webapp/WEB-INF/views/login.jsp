<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Student Login" />
<%@ include file="fragments/header.jspf" %>

<div class="container" style="max-width:440px">
  <div class="card">
    <h2>Student Login</h2>

    <%-- c:out escapes special characters, so nobody can inject HTML/JavaScript --%>
    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}" /></div></c:if>
    <c:if test="${not empty info}"><div class="alert info"><c:out value="${info}" /></div></c:if>

    <form method="post" action="${ctx}/login">
      <label class="field">Username
        <input type="text" name="username" value="<c:out value='${param.username}' />" required autofocus>
      </label>
      <label class="field">Password
        <input type="password" name="password" required>
      </label>
      <button class="btn block" type="submit">Login</button>
    </form>

    <p class="muted center" style="margin-top:18px">
      New here? <a href="${ctx}/register">Create an account</a>
    </p>
  </div>
</div>

<%@ include file="fragments/footer.jspf" %>
