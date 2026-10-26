package com.quiz.servlet;

import java.io.IOException;
import java.util.List;

import com.quiz.model.Question;
import com.quiz.util.AppConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Shows the CURRENT question (GET only, it never changes the quiz state).
 * Because it only reads from the session, refreshing the page is safe:
 * the same question appears and the timer continues from the remaining time.
 */
@WebServlet("/question")
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class QuestionServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        HttpSession session = req.getSession();

        List<Question> quiz = (List<Question>) session.getAttribute("quizQuestions");
        if (quiz == null) {
            // Edge case: opened /question directly without choosing a level
            resp.sendRedirect(ctx + "/levels?msg=nolevel");
            return;
        }
        if (Boolean.TRUE.equals(session.getAttribute("quizFinished"))) {
            // Edge case: quiz already over (e.g. user pressed Back) -> show the result
            resp.sendRedirect(ctx + "/result");
            return;
        }

        int index = (Integer) session.getAttribute("quizIndex");
        long startedAt = (Long) session.getAttribute("quizStartedAt");

        // The SERVER decides how much time is left (so refreshing cannot give extra time)
        long totalMillis = AppConfig.SECONDS_PER_QUESTION * 1000L;
        long millisLeft = Math.max(0, totalMillis - (System.currentTimeMillis() - startedAt));

        int total = quiz.size();
        req.setAttribute("question", quiz.get(index));
        req.setAttribute("index", index);               // 0-based, sent back as a hidden field
        req.setAttribute("number", index + 1);          // 1-based, shown to the user
        req.setAttribute("total", total);
        req.setAttribute("progress", index * 100 / total);
        req.setAttribute("level", session.getAttribute("quizLevel"));
        req.setAttribute("isLast", index == total - 1);
        req.setAttribute("millisLeft", millisLeft);
        req.setAttribute("totalMillis", totalMillis);
        req.getRequestDispatcher("/WEB-INF/views/question.jsp").forward(req, resp);
    }
}
