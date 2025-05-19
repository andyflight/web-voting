package com.example.webvoting.servlets.votings;

import com.example.webvoting.exceptions.VotingNotFoundException;
import com.example.webvoting.models.Voting;
import com.example.webvoting.services.VotingService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "VotingResultsServlet", urlPatterns = "/results/*")
public class VotingResultsServlet extends HttpServlet {

    @EJB
    private VotingService votingService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.length() > 1) {

            try {
                UUID votingId = UUID.fromString(pathInfo.substring(1));

                Map<String, Integer> results = votingService.getVotes(votingId);
                Voting voting = votingService.getVotingById(votingId);

                request.setAttribute("results", results);
                request.setAttribute("voting", voting);

                request.getRequestDispatcher("/WEB-INF/views/votingResults.jsp").forward(request, response);
            }
            catch (IllegalArgumentException e) {
                String error = "Invalid voting ID format";
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
            }
            catch (VotingNotFoundException e) {
                String error = "Voting not found";
                response.sendError(HttpServletResponse.SC_NOT_FOUND, error);
            }
        }
    }


}
