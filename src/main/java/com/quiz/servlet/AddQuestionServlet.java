package com.quiz.servlet;

import java.io.IOException;

import com.quiz.dao.QuestionDAO;
import com.quiz.util.AppConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Admin adds a new question. GET shows the form, POST validates and saves to questions.xml. */
@WebServlet("/admin/addquestion")
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class AddQuestionServlet extends HttpServlet {

    private final QuestionDAO questionDAO = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("saved".equals(req.getParameter("msg"))) {
            req.setAttribute("info", "Question added successfully!");
        }
        req.getRequestDispatcher("/WEB-INF/views/add-question.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String level = req.getParameter("level");
        String text = clean(req.getParameter("text"));
        String[] options = new String[4];
        for (int i = 0; i < 4; i++) {
            options[i] = clean(req.getParameter("option" + i));
        }
        String correctText = req.getParameter("correct");

        // ---- Validation ----
        String error = null;
        int correct = -1;
        if (!AppConfig.isValidLevel(level)) {
            error = "Please choose a level.";
        } else if (text.isEmpty()) {
            error = "Question text is required.";
        } else if (hasEmpty(options)) {
            error = "All four options are required.";
        } else {
            try {
                correct = Integer.parseInt(correctText);
            } catch (NumberFormatException e) {
                correct = -1;
            }
            if (correct < 0 || correct > 3) {
                error = "Please mark which option is correct.";
            }
        }

        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/WEB-INF/views/add-question.jsp").forward(req, resp);
            return;
        }

        questionDAO.add(level, text, options, correct);
        resp.sendRedirect(req.getContextPath() + "/admin/addquestion?msg=saved"); // Post/Redirect/Get
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean hasEmpty(String[] values) {
        for (String v : values) {
            if (v.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
