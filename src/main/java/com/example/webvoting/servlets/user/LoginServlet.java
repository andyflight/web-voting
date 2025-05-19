package com.example.webvoting.servlets.user;

import com.example.webvoting.models.User;
import com.example.webvoting.services.UserService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @EJB
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");

        if (username != null && !username.trim().isEmpty()) {
            User user = userService.getUserByName(username).orElse(userService.createUser(username));
            request.getSession().invalidate();
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId().toString());
            response.sendRedirect(request.getContextPath() + "/votings");
        } else {
            String error = "Invalid username. Please try again.";
            request.getSession().setAttribute("error", error);
            response.sendRedirect(request.getContextPath() + "/login");
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String error = (String) request.getSession().getAttribute("error");
        if (error != null) {
            request.getSession().removeAttribute("error");
            request.setAttribute("error", error);
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
}
