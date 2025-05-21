package com.example.webvoting.rest.controllers;


import com.example.webvoting.exceptions.UserAlreadyExistsException;
import com.example.webvoting.models.User;
import com.example.webvoting.rest.dto.LoginRequest;
import com.example.webvoting.services.UserService;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/users")
public class UserController {

    @EJB
    private UserService userService;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest, @Context HttpServletRequest request) {
        if (loginRequest == null || loginRequest.username == null || loginRequest.username.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Username cannot be empty\"}").build();
        }

        String username = loginRequest.username.trim();

        try {
            User user = userService.getUserByName(username)
                    .orElseGet(() -> userService.createUser(username));

            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId().toString());
            session.setAttribute("username", user.getName());

            return Response.ok(user).build();
        } catch (EJBException e) {
            Throwable cause = e.getCause();
            String errorMessage = (cause != null && cause.getMessage() != null) ? cause.getMessage() : e.getMessage();
            if (cause instanceof UserAlreadyExistsException) {
                return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + errorMessage + "\"}").build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Login failed: " + errorMessage + "\"}").build();
        } catch (UserAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.ok().entity("{\"message\":\"Logged out successfully\"}").build();
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userIdStr = (String) session.getAttribute("userId");
            String username = (String) session.getAttribute("username");

            if (userIdStr != null && username != null) {
                try {
                    UUID userId = UUID.fromString(userIdStr);
                    User currentUser = new User();
                    currentUser.setId(userId);
                    currentUser.setName(username);
                    return Response.ok(currentUser).build();
                } catch (IllegalArgumentException e) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Invalid user ID format in session\"}").build();
                }
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Not authenticated\"}").build();
    }
}
