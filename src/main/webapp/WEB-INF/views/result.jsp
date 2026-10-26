<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Result" />
<%@ include file="fragments/header.jspf" %>

<div class="container">
  <div class="card center">
    <h2>Quiz Completed! &#127881;</h2>
    <p class="muted"><span class="badge ${level.toLowerCase()}">${level}</span> level</p>

    <div class="score-ring" style="--pct:${percentage}"><span>${percentage}%</span></div>

    <h3>Score: ${score} / ${total}</h3>
    <p class="muted">
      <c:choose>
        <c:when test="${percentage >= 80}">Excellent work!</c:when>
        <c:when test="${percentage >= 50}">Good job &mdash; keep practising.</c:when>
        <c:otherwise>Don't give up &mdash; try again!</c:otherwise>
      </c:choose>
    </p>

    <div style="display:flex;gap:12px;justify-content:center;flex-wrap:wrap;margin-top:16px">
      <a class="btn" href="${ctx}/levels">&#8635; Restart / Try another level</a>
      <a class="btn secondary" href="${ctx}/myresults">My Results</a>
    </div>
  </div>

  <h3 style="margin-top:30px">Review your answers</h3>

  <%-- chosen[st.index] = option the student picked for question number st.index (-1 = none) --%>
  <c:forEach var="q" items="${questions}" varStatus="st">
    <c:set var="picked" value="${chosen[st.index]}" />
    <div class="review-item ${picked == q.correctIndex ? 'right' : 'wrong'}">
      <h4>
        Q${st.count}. <c:out value="${q.text}" />
        <c:choose>
          <c:when test="${picked == q.correctIndex}"><span class="tag" style="color:var(--good)">Correct</span></c:when>
          <c:when test="${picked == -1}"><span class="tag" style="color:var(--warn)">Not answered / time up</span></c:when>
          <c:otherwise><span class="tag" style="color:var(--bad)">Wrong</span></c:otherwise>
        </c:choose>
      </h4>
      <c:forEach var="opt" items="${q.options}" varStatus="o">
        <div class="review-opt ${o.index == q.correctIndex ? 'correct' : (o.index == picked ? 'picked-wrong' : '')}">
          <c:out value="${opt}" />
          <c:if test="${o.index == q.correctIndex}"> &#10003; correct answer</c:if>
          <c:if test="${o.index == picked && picked != q.correctIndex}"> &#10007; your answer</c:if>
        </div>
      </c:forEach>
    </div>
  </c:forEach>
</div>

<%@ include file="fragments/footer.jspf" %>
