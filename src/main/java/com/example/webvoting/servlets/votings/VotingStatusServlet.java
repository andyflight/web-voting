package com.example.webvoting.servlets.votings;

import com.example.webvoting.exceptions.VotingDataException;
import com.example.webvoting.exceptions.VotingNotFoundException;
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
import java.util.UUID;

@WebServlet(name = "VotingStatusServlet", urlPatterns = "/votings/status")
public class VotingStatusServlet extends HttpServlet {
    @EJB
    private VotingService votingService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String votingId = request.getParameter("votingId");
        String status = request.getParameter("status");
        String userId = (String) request.getAttribute("currentUserId");
        if (userId != null && votingId != null && status != null) {
            try{
                Voting voting = votingService.getVotingById(UUID.fromString(votingId));

                if (!voting.getCreator().getId().equals(UUID.fromString(userId))) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                if (status.equals("start")) {
                    votingService.startVoting(UUID.fromString(votingId));
                } else if (status.equals("stop")) {
                    votingService.stopVoting(UUID.fromString(votingId));
                } else {
                    throw new IllegalArgumentException("Invalid status value");
                }
                response.sendRedirect(request.getContextPath() + "/votings/my");
            } catch (IllegalArgumentException | VotingDataException | VotingNotFoundException | EJBException e) {
                String error = e.getMessage();
                request.getSession().setAttribute("error", error);
                response.sendRedirect(request.getContextPath() + "/votings");
            }
        } else {
            String error = "Invalid Input. Try Again!";
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
        }
    }
}
