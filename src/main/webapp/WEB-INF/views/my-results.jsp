<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Results" />
<%@ include file="fragments/header.jspf" %>

<div class="container wide">
  <div class="card">
    <h2>My Results</h2>

    <c:choose>
      <c:when test="${empty results}">
        <p class="muted">You have not taken any quiz yet.</p>
        <a class="btn" href="${ctx}/levels">Take your first quiz</a>
      </c:when>
      <c:otherwise>
        <p class="muted">${results.size()} attempt(s), newest first. Data comes from results.xml.</p>
        <div style="overflow-x:auto">
        <table>
          <tr><th>#</th><th>Date</th><th>Level</th><th>Score</th><th>Percentage</th></tr>
          <c:forEach var="r" items="${results}" varStatus="st">
            <tr>
              <td>${st.count}</td>
              <td><c:out value="${r.date}" /></td>
              <td><span class="badge ${r.level.toLowerCase()}"><c:out value="${r.level}" /></span></td>
              <td>${r.score} / ${r.total}</td>
              <td>
                <div class="bar" style="margin:0;width:140px;display:inline-block;vertical-align:middle">
                  <div class="bar-fill" style="width:${r.percentage}%"></div>
                </div>
                &nbsp;${r.percentage}%
              </td>
            </tr>
          </c:forEach>
        </table>
        </div>
        <p style="margin-top:20px"><a class="btn" href="${ctx}/levels">Take another quiz</a></p>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<%@ include file="fragments/footer.jspf" %>
