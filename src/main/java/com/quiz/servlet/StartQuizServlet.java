package com.quiz.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.quiz.dao.QuestionDAO;
import com.quiz.model.Question;
import com.quiz.util.AppConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Starts a NEW quiz: picks random questions of the chosen level and stores the quiz state in the session.
 *
 * Session attributes used by the quiz (the "memory" of the quiz between requests):
 *   quizLevel      String          "Easy" / "Medium" / "Hard"
 *   quizQuestions  List<Question>  the questions chosen for this attempt
 *   quizIndex      Integer         0-based number of the CURRENT question
 *   quizScore      Integer         correct answers so far
 *   quizChosen     int[]           option picked per question (-1 = not answered / time up)
 *   quizStartedAt  Long            millisecond time when the current question was shown
 *   quizFinished   Boolean         true after the last question was answered
 */
@WebServlet("/start")
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class StartQuizServlet extends HttpServlet {

    private final QuestionDAO questionDAO = new QuestionDAO();

    /** Typing /start in the address bar sends a GET: just go back to the level page. */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/levels");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ctx = req.getContextPath();
        String level = req.getParameter("level");

        // Edge case: no level chosen (or a made-up value)
        if (!AppConfig.isValidLevel(level)) {
            resp.sendRedirect(ctx + "/levels?msg=nolevel");
            return;
        }

        List<Question> pool = new ArrayList<>(questionDAO.getByLevel(level));
        if (pool.isEmpty()) {
            resp.sendRedirect(ctx + "/levels?msg=empty");
            return;
        }

        // Shuffle, then take up to 10, so every attempt is a bit different
        Collections.shuffle(pool);
        List<Question> quiz = new ArrayList<>(pool.subList(0, Math.min(AppConfig.QUESTIONS_PER_QUIZ, pool.size())));

        // Store the fresh quiz state in the session (this replaces any older quiz = "restart")
        HttpSession session = req.getSession();
        session.setAttribute("quizLevel", level);
        session.setAttribute("quizQuestions", quiz);
        session.setAttribute("quizIndex", 0);
        session.setAttribute("quizScore", 0);
        int[] chosen = new int[quiz.size()];
        Arrays.fill(chosen, -1);
        session.setAttribute("quizChosen", chosen);
        session.setAttribute("quizStartedAt", System.currentTimeMillis());
        session.setAttribute("quizFinished", false);

        resp.sendRedirect(ctx + "/question");
    }
}
