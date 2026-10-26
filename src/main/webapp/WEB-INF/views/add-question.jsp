<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Add Question" />
<%@ include file="fragments/header.jspf" %>

<div class="container">
  <div class="card">
    <h2>Add a New Question</h2>

    <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}" /></div></c:if>
    <c:if test="${not empty info}"><div class="alert info"><c:out value="${info}" /></div></c:if>

    <form method="post" action="${ctx}/admin/addquestion">
      <label class="field">Level
        <select name="level" required>
          <option value="">-- choose level --</option>
          <c:forEach var="lvl" items="Easy,Medium,Hard">
            <option value="${lvl}" ${param.level == lvl ? 'selected' : ''}>${lvl}</option>
          </c:forEach>
        </select>
      </label>

      <label class="field">Question
        <textarea name="text" required><c:out value="${param.text}" /></textarea>
      </label>

      <div class="row-2">
        <label class="field">Option A <input type="text" name="option0" value="<c:out value='${param.option0}' />" required></label>
        <label class="field">Option B <input type="text" name="option1" value="<c:out value='${param.option1}' />" required></label>
        <label class="field">Option C <input type="text" name="option2" value="<c:out value='${param.option2}' />" required></label>
        <label class="field">Option D <input type="text" name="option3" value="<c:out value='${param.option3}' />" required></label>
      </div>

      <p style="font-weight:600;font-size:0.92rem;margin-bottom:6px">Correct option</p>
      <div class="correct-pick">
        <c:forEach var="letter" items="A,B,C,D" varStatus="st">
          <label><input type="radio" name="correct" value="${st.index}" ${param.correct == st.index ? 'checked' : ''} required> ${letter}</label>
        </c:forEach>
      </div>

      <button class="btn" type="submit">Save Question</button>
      <a class="btn secondary" href="${ctx}/admin/dashboard">Back</a>
    </form>
  </div>
</div>

<%@ include file="fragments/footer.jspf" %>
