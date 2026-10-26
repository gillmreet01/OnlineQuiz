package com.quiz.servlet;

import java.io.IOException;

import com.quiz.dao.UserDAO;
import com.quiz.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * One servlet, two URLs:
 *   /login      -> student login page (login.jsp)
 *   /adminlogin -> admin login page   (admin-login.jsp)
 * GET shows the form, POST checks the username and password.
 */
@WebServlet({"/login", "/adminlogin"})
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Already logged in? Go straight to the right home page.
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + homeFor((User) session.getAttribute("user")));
            return;
        }

        // Short message code from the URL (?msg=...) -> readable text. We never print the raw parameter.
        String msg = req.getParameter("msg");
        if ("registered".equals(msg)) {
            req.setAttribute("info", "Registration successful! Please log in.");
        } else if ("login".equals(msg)) {
            req.setAttribute("info", "Please log in first.");
        } else if ("logout".equals(msg)) {
            req.setAttribute("info", "You have been logged out.");
        } else if ("denied".equals(msg)) {
            req.setAttribute("error", "Admin access only. Please log in as admin.");
        }
        req.getRequestDispatcher(viewFor(req)).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean adminLogin = isAdminLogin(req);
        String username = req.getParameter("username") == null ? "" : req.getParameter("username").trim();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        if (username.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Please enter username and password.");
            req.getRequestDispatcher(viewFor(req)).forward(req, resp);
            return;
        }

        User user = userDAO.authenticate(username, password);

        // The student form accepts only students, the admin form only admins.
        // One generic message, so nobody can learn which usernames exist.
        if (user == null || user.isAdmin() != adminLogin) {
            req.setAttribute("error", "Invalid username or password.");
            req.getRequestDispatcher(viewFor(req)).forward(req, resp);
            return;
        }

        // Login OK. Destroy any old session and start a fresh one (prevents session fixation).
        HttpSession old = req.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("user", user);

        // Redirect (not forward) so refreshing does not resend the password form.
        resp.sendRedirect(req.getContextPath() + homeFor(user));
    }

    private boolean isAdminLogin(HttpServletRequest req) {
        return "/adminlogin".equals(req.getServletPath());
    }

    private String viewFor(HttpServletRequest req) {
        return isAdminLogin(req) ? "/WEB-INF/views/admin-login.jsp" : "/WEB-INF/views/login.jsp";
    }

    private String homeFor(User user) {
        return user.isAdmin() ? "/admin/dashboard" : "/levels";
    }
}
