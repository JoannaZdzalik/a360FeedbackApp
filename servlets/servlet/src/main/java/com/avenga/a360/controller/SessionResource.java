package com.avenga.a360.controller;

import com.avenga.a360.dto.EditDto.SessionEditDto;
import com.avenga.a360.dto.SessionDto;
import com.avenga.a360.model.response.Status;
import com.avenga.a360.service.SessionService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/sessions")
public class SessionResource {
    @Inject
    SessionService sessionService;

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSessionFromSessionDto(final SessionDto sessionDto) {
        Status status = sessionService.createSession(sessionDto, sessionDto.getParticipantList(), sessionDto.getQuestionList());
        if (status.getStatus().equals("success")) {
            return Response.status(Response.Status.CREATED).entity(status).build();
        } else return Response.status(Response.Status.BAD_REQUEST).entity(status).build();
    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSessions() {
        List<SessionDto> sessionDtoList = sessionService.findAllSessions();
        if (sessionDtoList != null && !sessionDtoList.isEmpty()) {
            return Response.status(Response.Status.OK).entity(sessionDtoList).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("No sessions").build();
        }
    }

    @Path("/get/{uid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSessionByParticipantUid(@PathParam("uid") String uid) {
        SessionDto sessionDto = sessionService.findSessionByParticipantUid(uid);
        if (sessionDto != null) {
            return Response.status(Response.Status.OK).entity(sessionDto).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Session does not exist").build();
        }
    }

    @Path("/remove/{id}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteSession(@PathParam("id") Long id) {
        sessionService.removeSession(id);
        return Response.status(Response.Status.OK).entity("Session deleted").build();
    }

    @Path("/edit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editUserRole(SessionEditDto sessionEditDto) {
        boolean status = sessionService.updateSession(sessionEditDto);
        if (status) {
            return Response.status(Response.Status.CREATED).entity("Session isActive editted").build();
        } else return Response.status(Response.Status.BAD_REQUEST).entity("Session isActive not editted").build();
    }
}
