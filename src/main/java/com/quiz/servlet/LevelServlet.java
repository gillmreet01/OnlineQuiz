package com.quiz.servlet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.quiz.dao.QuestionDAO;
import com.quiz.util.AppConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Shows the Easy / Medium / Hard cards. This is the page where every quiz starts. */
@WebServlet("/levels")
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class LevelServlet extends HttpServlet {

    private final QuestionDAO questionDAO = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // How many questions are available per level (shown on the cards)
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String level : AppConfig.LEVELS) {
            counts.put(level, questionDAO.getByLevel(level).size());
        }
        req.setAttribute("counts", counts);
        req.setAttribute("perQuiz", AppConfig.QUESTIONS_PER_QUIZ);
        req.setAttribute("seconds", AppConfig.SECONDS_PER_QUESTION);

        // Messages sent by other servlets via ?msg=...
        String msg = req.getParameter("msg");
        if ("nolevel".equals(msg)) {
            req.setAttribute("error", "Please choose a level to start the quiz.");
        } else if ("empty".equals(msg)) {
            req.setAttribute("error", "That level has no questions yet. Ask the admin to add some.");
        }
        req.getRequestDispatcher("/WEB-INF/views/select-level.jsp").forward(req, resp);
    }
}
