<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register" />
<%@ include file="fragments/header.jspf" %>

<div class="container" style="max-width:480px">
  <div class="card">
    <h2>Create Student Account</h2>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}" /></div></c:if>

    <form method="post" action="${ctx}/register">
      <%-- ${param.x} refills the field with what the user typed before the error --%>
      <label class="field">Full name
        <input type="text" name="fullName" maxlength="50" value="<c:out value='${param.fullName}' />" required>
      </label>
      <label class="field">Username (3-20 letters, digits or _)
        <input type="text" name="username" maxlength="20" value="<c:out value='${param.username}' />" required>
      </label>
      <label class="field">Password (min 4 characters)
        <input type="password" name="password" required>
      </label>
      <label class="field">Confirm password
        <input type="password" name="confirm" required>
      </label>
      <button class="btn block" type="submit">Register</button>
    </form>

    <p class="muted center" style="margin-top:18px">
      Already registered? <a href="${ctx}/login">Login</a>
    </p>
  </div>
</div>

<%@ include file="fragments/footer.jspf" %>
