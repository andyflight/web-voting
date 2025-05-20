package com.example.webvoting.servlets.votings;

import com.example.webvoting.exceptions.VotingDataException;
import com.example.webvoting.models.Voting;
import com.example.webvoting.services.VotingService;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "UserVotingsServlet", urlPatterns = "/votings/my")
public class UserVotingsServlet extends HttpServlet {
    @EJB
    private VotingService votingService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = (String) request.getAttribute("currentUserId");
        if (userId != null) {
            try {
                List<Voting> votings = votingService.getVotingsByCreatorId(UUID.fromString(userId));
                request.setAttribute("votings", votings);
                request.getRequestDispatcher("/WEB-INF/views/userVotings.jsp").forward(request, response);
            } catch (IllegalArgumentException | VotingDataException | EJBException e) {
                String error = e.getMessage();
                request.getSession().setAttribute("error", error);
                response.sendRedirect(request.getContextPath() + "/votings");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

}
