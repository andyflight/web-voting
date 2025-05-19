package com.example.webvoting.servlets.votings;

import com.example.webvoting.models.Voting;
import com.example.webvoting.services.VotingService;
import com.example.webvoting.services.impl.VotingServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "VotingListServlet", urlPatterns = {"/votings"})
public class VotingListServlet extends HttpServlet {

    private VotingService votingService = new VotingServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Voting> votings = votingService.getAllVotings();
        request.setAttribute("votings", votings);
        request.getRequestDispatcher("/WEB-INF/views/votingList.jsp").forward(request, response);
    }

}
