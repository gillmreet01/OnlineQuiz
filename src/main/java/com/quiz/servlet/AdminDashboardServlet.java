package com.quiz.servlet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.quiz.dao.QuestionDAO;
import com.quiz.model.Question;
import com.quiz.util.AppConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Admin home: how many questions each level has, plus a table of all questions. */
@WebServlet("/admin/dashboard")
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class AdminDashboardServlet extends HttpServlet {

    private final QuestionDAO questionDAO = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Question> all = questionDAO.getAll();

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String level : AppConfig.LEVELS) {
            counts.put(level, 0);
        }
        for (Question q : all) {
            counts.merge(q.getLevel(), 1, Integer::sum);
        }

        req.setAttribute("questions", all);
        req.setAttribute("counts", counts);
        req.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(req, resp);
    }
}
