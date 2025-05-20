package com.example.webvoting.servlets.vote;

import com.example.webvoting.exceptions.*;
import com.example.webvoting.services.VotingService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;


@WebServlet(name = "CastVoteServlet", urlPatterns = "/votings/vote")
public class CastVoteServlet extends HttpServlet {

    @EJB
    private VotingService votingService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String votingId = request.getParameter("votingId");
        String candidateId = request.getParameter("candidateId");
        String userId = (String) request.getAttribute("currentUserId");

        if (votingId != null && candidateId != null && userId != null) {
            try {
                votingService.vote(UUID.fromString(votingId), UUID.fromString(userId), UUID.fromString(candidateId));
                String message = "Vote cast successfully!";
                request.getSession().setAttribute("message", message);
                response.sendRedirect(request.getContextPath() + "/votings/" + votingId);
            } catch (jakarta.ejb.EJBException e) {
                Throwable cause = e.getCause();
                if (cause instanceof UserHasAlreadyVotedException || cause instanceof VotingNotActiveException) {
                    String error = cause.getMessage();
                    request.getSession().setAttribute("error", error);
                    response.sendRedirect(request.getContextPath() + "/votings/" + votingId);
                } else {
                    String error = e.getMessage();
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
                }
            } catch (VotingNotFoundException | CandidateNotFoundException | IllegalArgumentException |
                     VotingDataException e) {
                String error = e.getMessage();
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
            } catch (UserHasAlreadyVotedException | VotingNotActiveException e) {
                String error = e.getMessage();
                request.getSession().setAttribute("error", error);
                response.sendRedirect(request.getContextPath() + "/votings/" + votingId);
            }
        } else {
            String error = "Invalid Input. Try Again!";
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
        }
    }
}