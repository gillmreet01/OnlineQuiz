package com.quiz.servlet;

import java.io.IOException;
import java.util.List;

import com.quiz.dao.ResultDAO;
import com.quiz.model.Question;
import com.quiz.model.User;
import com.quiz.util.AppConfig;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Receives the answer for the current question (POST), updates the score, and moves on.
 * The browser calls this when the student clicks Next OR when the 10-second timer runs out.
 * After saving the state it always REDIRECTS, so pressing Refresh never re-submits an answer.
 */
@WebServlet("/answer")
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class AnswerServlet extends HttpServlet {

    private final ResultDAO resultDAO = new ResultDAO();

    /** Typing /answer in the address bar (GET) is not allowed: show the current question instead. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/question");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ctx = req.getContextPath();
        HttpSession session = req.getSession();

        List<Question> quiz = (List<Question>) session.getAttribute("quizQuestions");
        if (quiz == null) {
            resp.sendRedirect(ctx + "/levels?msg=nolevel");
            return;
        }
        if (Boolean.TRUE.equals(session.getAttribute("quizFinished"))) {
            resp.sendRedirect(ctx + "/result");
            return;
        }

        int index = (Integer) session.getAttribute("quizIndex");

        // Edge case (Back button / double click): the form says which question it belongs to.
        // If that is not the question the server expects, ignore it and show the real current one.
        if (!String.valueOf(index).equals(req.getParameter("qIndex"))) {
            resp.sendRedirect(ctx + "/question");
            return;
        }

        // Which option did the student pick? -1 means nothing (skipped or time ran out).
        int picked = -1;
        try {
            picked = Integer.parseInt(req.getParameter("answer"));
        } catch (NumberFormatException e) {
            picked = -1; // parameter missing = nothing selected
        }
        if (picked < 0 || picked > 3) {
            picked = -1;
        }

        // Server-side timer check: if the answer arrives much later than 10 seconds
        // (for example JavaScript was switched off), it does not count.
        long startedAt = (Long) session.getAttribute("quizStartedAt");
        long allowedMillis = (AppConfig.SECONDS_PER_QUESTION + AppConfig.GRACE_SECONDS) * 1000L;
        if (System.currentTimeMillis() - startedAt > allowedMillis) {
            picked = -1;
        }

        // Record the answer and update the score
        int[] chosen = (int[]) session.getAttribute("quizChosen");
        chosen[index] = picked;
        int score = (Integer) session.getAttribute("quizScore");
        if (picked == quiz.get(index).getCorrectIndex()) {
            score++;
            session.setAttribute("quizScore", score);
        }

        // Move to the next question, or finish
        index++;
        if (index < quiz.size()) {
            session.setAttribute("quizIndex", index);
            session.setAttribute("quizStartedAt", System.currentTimeMillis()); // timer restarts
            resp.sendRedirect(ctx + "/question");
        } else {
            // Last question answered: save the result ONCE and mark the quiz as finished.
            User user = (User) session.getAttribute("user");
            resultDAO.save(user.getUsername(), (String) session.getAttribute("quizLevel"), score, quiz.size());
            session.setAttribute("quizFinished", true);
            resp.sendRedirect(ctx + "/result");
        }
    }
}
