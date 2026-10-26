package com.quiz.servlet;

import java.io.IOException;

import com.quiz.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Student registration. GET shows the form, POST validates it and saves to users.xml. */
@WebServlet("/register")
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = clean(req.getParameter("fullName"));
        String username = clean(req.getParameter("username"));
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirm") == null ? "" : req.getParameter("confirm");

        // ---- Server-side validation (never trust only the browser) ----
        String error = null;
        if (fullName.isEmpty() || fullName.length() > 50) {
            error = "Full name is required (max 50 characters).";
        } else if (!username.matches("[A-Za-z0-9_]{3,20}")) {
            error = "Username must be 3-20 letters, digits or underscore.";
        } else if (password.length() < 4) {
            error = "Password must be at least 4 characters.";
        } else if (!password.equals(confirm)) {
            error = "Passwords do not match.";
        } else if (!userDAO.registerStudent(username, password, fullName)) {
            error = "That username is already taken. Please choose another.";
        }

        if (error != null) {
            // Show the form again with the message. The JSP refills fullName/username from ${param}.
            req.setAttribute("error", error);
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        // Success: redirect so a page refresh does not register the same user twice.
        resp.sendRedirect(req.getContextPath() + "/login?msg=registered");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
