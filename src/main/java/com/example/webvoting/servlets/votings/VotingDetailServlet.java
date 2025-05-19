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
import java.util.UUID;

@WebServlet(name = "VotingDetailServlet", urlPatterns = {"/votings/*"})
public class VotingDetailServlet extends HttpServlet {
    @EJB
    private VotingService votingService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.length() > 1) {

            try {
                UUID votingId = UUID.fromString(pathInfo.substring(1));

                Voting voting = votingService.getVotingById(votingId);

                String votingUrl = votingService.generateVotingLink(request, voting.getId());
                String resultUrl = votingService.generateResultsLink(request, voting.getId());

                String message = (String) request.getSession().getAttribute("message");
                String error = (String) request.getSession().getAttribute("error");
                if (message != null) {
                    request.setAttribute("message", message);
                    request.getSession().removeAttribute("message");
                }
                if (message != null) {
                    request.setAttribute("error", error);
                    request.getSession().removeAttribute("error");
                }

                request.setAttribute("message", message);
                request.setAttribute("votingUrl", votingUrl);
                request.setAttribute("resultUrl", resultUrl);
                request.setAttribute("voting", voting);
                request.getRequestDispatcher("/WEB-INF/views/votingDetail.jsp").forward(request, response);
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
