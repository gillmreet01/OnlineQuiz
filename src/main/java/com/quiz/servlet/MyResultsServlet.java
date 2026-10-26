package com.quiz.servlet;

import java.io.IOException;

import com.quiz.dao.ResultDAO;
import com.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** "My Results": lists the logged-in student's past attempts from results.xml. */
@WebServlet("/myresults")
@SuppressWarnings("serial") // Eclipse asks for serialVersionUID; we never serialize servlets
public class MyResultsServlet extends HttpServlet {

    private final ResultDAO resultDAO = new ResultDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        req.setAttribute("results", resultDAO.getByUsername(user.getUsername()));
        req.getRequestDispatcher("/WEB-INF/views/my-results.jsp").forward(req, resp);
    }
}
