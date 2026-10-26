<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Choose Level" />
<%@ include file="fragments/header.jspf" %>

<div class="container wide">
  <div class="card">
    <h2>Choose your level</h2>
    <p class="muted">
      Each quiz has up to ${perQuiz} random questions. You get ${seconds} seconds per question &mdash;
      when the timer hits zero, the next question appears automatically.
    </p>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}" /></div></c:if>

    <div class="level-grid">
      <c:forEach var="lvl" items="Easy,Medium,Hard">
        <div class="level-card ${lvl.toLowerCase()}">
          <h3>${lvl}</h3>
          <p>${counts[lvl]} questions available</p>
          <%-- POST form: the chosen level travels in the hidden field --%>
          <form method="post" action="${ctx}/start">
            <input type="hidden" name="level" value="${lvl}">
            <button class="btn block" type="submit">Start ${lvl}</button>
          </form>
        </div>
      </c:forEach>
    </div>
  </div>
</div>

<%@ include file="fragments/footer.jspf" %>
