<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Oops" />
<%@ include file="fragments/header.jspf" %>

<div class="container" style="max-width:520px">
  <div class="card center">
    <h2>Oops! Something went wrong &#128533;</h2>
    <p class="muted">The page you wanted does not exist, or an unexpected error happened.
       No technical details are shown to users.</p>
    <a class="btn" href="${ctx}/">Go to Home</a>
  </div>
</div>

<%@ include file="fragments/footer.jspf" %>
