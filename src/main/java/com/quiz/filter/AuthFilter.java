package com.quiz.filter;

import java.io.IOException;

import com.quiz.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The "security guard". Every request to a protected URL passes through here FIRST.
 *   - not logged in            -> sent to the login page
 *   - student opens /admin/... -> sent to the admin login page
 *   - admin opens student page -> sent to the admin dashboard
 * It also tells the browser not to cache these pages, so after Logout the Back button
 * cannot show a private page again.
 */
@WebFilter(urlPatterns = {"/levels", "/start", "/question", "/answer", "/result", "/myresults", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String ctx = req.getContextPath();

        // Do not let the browser cache protected pages
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        // false = do NOT create a new session if there is none
        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        boolean adminArea = req.getServletPath().startsWith("/admin");

        if (user == null) {
            // Not logged in
            resp.sendRedirect(ctx + (adminArea ? "/adminlogin" : "/login") + "?msg=login");
        } else if (adminArea && !user.isAdmin()) {
            // A student is trying to open an admin page
            resp.sendRedirect(ctx + "/adminlogin?msg=denied");
        } else if (!adminArea && user.isAdmin()) {
            // An admin is trying to take the quiz
            resp.sendRedirect(ctx + "/admin/dashboard");
        } else {
            chain.doFilter(request, response); // allowed: continue to the servlet
        }
    }
}
