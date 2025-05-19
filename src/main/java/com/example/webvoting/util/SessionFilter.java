package com.example.webvoting.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/votings/vote", "/votings/my", "/votings/create", "/votings/status"})
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) res;

        HttpSession session = httpRequest.getSession(false);

        String id = (session != null) ? (String) session.getAttribute("userId") : null;

        if (id == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        req.removeAttribute("currentUserId");
        req.setAttribute("currentUserId", id);
        chain.doFilter(httpRequest, httpResponse);
    }
}
