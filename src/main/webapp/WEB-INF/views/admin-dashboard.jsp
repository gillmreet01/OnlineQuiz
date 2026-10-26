<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Dashboard" />
<%@ include file="fragments/header.jspf" %>

<div class="container wide">
  <div class="card">
    <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px">
      <h2 style="margin:0">Admin Dashboard</h2>
      <a class="btn" href="${ctx}/admin/addquestion">+ Add Question</a>
    </div>

    <div class="stats" style="margin-top:20px">
      <c:forEach var="entry" items="${counts}">
        <div class="stat"><b>${entry.value}</b><span>${entry.key} questions</span></div>
      </c:forEach>
      <div class="stat"><b>${questions.size()}</b><span>Total questions</span></div>
    </div>

    <h3>All questions (stored in questions.xml)</h3>
    <div style="overflow-x:auto">
    <table>
      <tr><th>ID</th><th>Level</th><th>Question</th><th>Correct answer</th></tr>
      <c:forEach var="q" items="${questions}">
        <tr>
          <td>${q.id}</td>
          <td><span class="badge ${q.level.toLowerCase()}"><c:out value="${q.level}" /></span></td>
          <td><c:out value="${q.text}" /></td>
          <td><c:out value="${q.options[q.correctIndex]}" /></td>
        </tr>
      </c:forEach>
    </table>
    </div>
  </div>
</div>

<%@ include file="fragments/footer.jspf" %>
