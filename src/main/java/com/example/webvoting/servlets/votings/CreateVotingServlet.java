package com.example.webvoting.servlets.votings;

import com.example.webvoting.services.VotingService;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "CreateVotingServlet", urlPatterns = "/votings/create")
public class CreateVotingServlet extends HttpServlet {
    @EJB
    private VotingService votingService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String[] candidates = request.getParameterValues("candidates");
        String userId = (String) request.getAttribute("currentUserId");
        if (title != null && candidates != null && userId != null) {
            try {
                List<String> candidateList = Arrays.asList(candidates);
                votingService.createVoting(title, candidateList, UUID.fromString(userId));
                String message = "Voting created successfully!";
                request.getSession().setAttribute("message", message);
                response.sendRedirect(request.getContextPath() + "/votings");
            } catch (IllegalArgumentException | EJBException e) {
                String error = e.getMessage();
                request.getSession().setAttribute("error", error);
                response.sendRedirect(request.getContextPath() + "/votings/create");
            }
        } else {
            String error = "Invalid Input. Try Again!";
            request.getSession().setAttribute("error", error);
            response.sendRedirect(request.getContextPath() + "/votings/create");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String error = (String) request.getSession().getAttribute("error");
        if (error != null) {
            request.setAttribute("error", error);
            request.getSession().removeAttribute("error");
        }

        request.getRequestDispatcher("/WEB-INF/views/votingCreate.jsp").forward(request, response);
    }
}
