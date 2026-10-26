package com.quiz.servlet;

import java.io.IOException;
import java.util.List;

import com.quiz.model.Question;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Shows the result page. It only READS the finished quiz from the session; the result was already
 * saved to results.xml by AnswerServlet. So refreshing this page never saves a duplicate.
 */
@WebServlet("/result")
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class ResultServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        HttpSession session = req.getSession();

        List<Question> quiz = (List<Question>) session.getAttribute("quizQuestions");
        if (quiz == null) {
            resp.sendRedirect(ctx + "/levels?msg=nolevel"); // no quiz taken yet
            return;
        }
        if (!Boolean.TRUE.equals(session.getAttribute("quizFinished"))) {
            resp.sendRedirect(ctx + "/question"); // quiz not finished: go back to it
            return;
        }

        int score = (Integer) session.getAttribute("quizScore");
        int total = quiz.size();

        req.setAttribute("score", score);
        req.setAttribute("total", total);
        req.setAttribute("percentage", (int) (score * 100.0 / total));
        req.setAttribute("level", session.getAttribute("quizLevel"));
        req.setAttribute("questions", quiz);
        req.setAttribute("chosen", session.getAttribute("quizChosen"));
        req.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(req, resp);
    }
}
