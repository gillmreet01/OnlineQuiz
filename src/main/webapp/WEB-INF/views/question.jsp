<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Question ${number}" />
<%@ include file="fragments/header.jspf" %>

<div class="container">
  <div class="card">

    <div class="quiz-top">
      <span><span class="badge ${level.toLowerCase()}">${level}</span> &nbsp; Question ${number} of ${total}</span>
      <%-- data-millis / data-total are read by timer.js --%>
      <span id="timer" class="timer-pill" data-millis="${millisLeft}" data-total="${totalMillis}">
        &#9201; <span id="timerLabel">&hellip;</span>
      </span>
    </div>

    <%-- thin bar that shrinks while time runs out --%>
    <div class="bar"><div id="timerFill" class="bar-fill timer" style="width:100%"></div></div>

    <%-- quiz progress bar: how many questions are already done --%>
    <div class="bar"><div class="bar-fill" style="width:${progress}%"></div></div>
    <div class="progress-text">Progress: ${progress}%</div>

    <form id="quizForm" method="post" action="${ctx}/answer">
      <%-- tells the server WHICH question this form belongs to (protects against Back button) --%>
      <input type="hidden" name="qIndex" value="${index}">

      <h2 class="question-text"><c:out value="${question.text}" /></h2>

      <c:forEach var="opt" items="${question.options}" varStatus="st">
        <label class="option">
          <input type="radio" name="answer" value="${st.index}">
          <span><c:out value="${opt}" /></span>
        </label>
      </c:forEach>

      <div class="quiz-actions">
        <a class="btn secondary" href="${ctx}/levels">Restart</a>
        <button id="nextBtn" class="btn" type="submit">${isLast ? 'Finish' : 'Next'} &rarr;</button>
      </div>
    </form>

  </div>
</div>

<script src="${ctx}/js/timer.js"></script>
<%@ include file="fragments/footer.jspf" %>
