package com.example.webvoting.rest.controllers;


import com.example.webvoting.exceptions.*;
import com.example.webvoting.models.Voting;
import com.example.webvoting.rest.dto.CreateVotingRequest;
import com.example.webvoting.rest.dto.VoteRequest;
import com.example.webvoting.rest.dto.VotingStatusUpdateRequest;
import com.example.webvoting.services.VotingService;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/votings")
public class VotingController {

    @EJB
    private VotingService votingService;

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private UriInfo uriInfo;

    private UUID getCurrentUserId() {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            String userIdStr = (String) session.getAttribute("userId");
            if (userIdStr != null) {
                try {
                    return UUID.fromString(userIdStr);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
        return null;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVoting(CreateVotingRequest createVotingRequest) {
        UUID creatorId = getCurrentUserId();
        if (creatorId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"User not authenticated\"}").build();
        }

        if (createVotingRequest == null || createVotingRequest.title == null || createVotingRequest.title.isBlank() ||
                createVotingRequest.candidateNames == null || createVotingRequest.candidateNames.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Title and candidate names are required\"}").build();
        }

        try {
            Voting voting = votingService.createVoting(createVotingRequest.title, createVotingRequest.candidateNames, creatorId);
            URI location = uriInfo.getAbsolutePathBuilder().path(voting.getId().toString()).build();
            return Response.created(location).entity(voting).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
        catch (VotingDataException | EJBException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error creating voting: " + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/my")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyVotings() {
        UUID creatorId = getCurrentUserId();
        if (creatorId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"User not authenticated\"}").build();
        }

        try {
            List<Voting> votings = votingService.getVotingsByCreatorId(creatorId);
            return Response.ok(votings).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (EJBException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error retrieving user votings: " + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVotings(@QueryParam("page") Integer page,
                                  @QueryParam("size") Integer size,
                                  @QueryParam("isActive") Boolean isActive) {
        try {
            List<Voting> votings;
            if (page != null && size != null) {
                votings = votingService.getAllVotings(page, size, isActive);
            } else if (page == null && size == null && isActive == null) {
                votings = votingService.getAllVotings();
            } else if (page == null && size == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"If isActive is provided, page and size must also be provided for filtering and pagination.\"}")
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Both page and size must be provided for pagination, or none if no pagination/filtering by active status is needed.\"}")
                        .build();
            }
            return Response.ok(votings).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (VotingDataException | EJBException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error retrieving votings: " + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVotingById(@PathParam("id") String idStr) {
        UUID votingId;
        try {
            votingId = UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Invalid voting ID format\"}").build();
        }

        try {
            Voting voting = votingService.getVotingById(votingId);
            return Response.ok(voting).build();
        } catch (VotingNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
        catch (EJBException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error retrieving voting: " + e.getMessage() + "\"}").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteVoting(@PathParam("id") String idStr) {
        UUID currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"User not authenticated\"}").build();
        }

        UUID votingId;
        try {
            votingId = UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Invalid voting ID format\"}").build();
        }

        try {
            Voting voting = votingService.getVotingById(votingId);
            if (!voting.getCreator().getId().equals(currentUserId)) {
                return Response.status(Response.Status.FORBIDDEN).entity("{\"error\":\"User not authorized to delete this voting\"}").build();
            }
            votingService.deleteVoting(votingId);
            return Response.noContent().build();
        } catch (VotingNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (EJBException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error deleting voting: " + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/{id}/vote")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response castVote(@PathParam("id") String votingIdStr, VoteRequest voteRequest) {
        UUID userId = getCurrentUserId();
        if (userId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"User not authenticated\"}").build();
        }

        UUID votingId;
        UUID candidateId;
        try {
            votingId = UUID.fromString(votingIdStr);
            if (voteRequest == null || voteRequest.candidateId == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Candidate ID is required\"}").build();
            }
            candidateId = UUID.fromString(voteRequest.candidateId);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Invalid ID format for voting or candidate\"}").build();
        }

        try {
            votingService.vote(votingId, userId, candidateId);
            return Response.ok().entity("{\"message\":\"Vote cast successfully\"}").build();
        } catch (VotingNotFoundException | CandidateNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (UserHasAlreadyVotedException e) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (VotingNotActiveException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (VotingDataException | EJBException e) {
            Throwable cause = (e instanceof EJBException) ? e.getCause() : e;
            if (cause instanceof UserHasAlreadyVotedException) {
                return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + cause.getMessage() + "\"}").build();
            } else if (cause instanceof VotingNotActiveException) {
                return Response.status(Response.Status.FORBIDDEN).entity("{\"error\":\"" + cause.getMessage() + "\"}").build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error casting vote: " + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVotingStatus(@PathParam("id") String votingIdStr, VotingStatusUpdateRequest statusRequest) {
        UUID currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"User not authenticated\"}").build();
        }

        UUID votingId;
        try {
            votingId = UUID.fromString(votingIdStr);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Invalid voting ID format\"}").build();
        }

        if (statusRequest == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Status update payload is required\"}").build();
        }

        try {
            Voting voting = votingService.getVotingById(votingId);
            if (!voting.getCreator().getId().equals(currentUserId)) {
                return Response.status(Response.Status.FORBIDDEN).entity("{\"error\":\"User not authorized to change status of this voting\"}").build();
            }

            if (statusRequest.isActive) {
                votingService.startVoting(votingId);
            } else {
                votingService.stopVoting(votingId);
            }
            Voting updatedVoting = votingService.getVotingById(votingId); // Повертаємо оновлений ресурс
            return Response.ok(updatedVoting).build();
        } catch (VotingNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
        catch (EJBException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error updating voting status: " + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{id}/results")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVotingResults(@PathParam("id") String idStr) {
        UUID votingId;
        try {
            votingId = UUID.fromString(idStr);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Invalid voting ID format\"}").build();
        }

        try {
            Map<String, Integer> results = votingService.getVotes(votingId);
            return Response.ok(results).build();
        } catch (VotingNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
        catch (EJBException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error retrieving voting results: " + e.getMessage() + "\"}").build();
        }
    }
}