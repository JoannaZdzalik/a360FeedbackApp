package com.avenga.a360.controller;

import com.avenga.a360.dto.AnswerDto;
import com.avenga.a360.dto.AnswerSessionDto;
import com.avenga.a360.dto.ParticipantDto;
import com.avenga.a360.model.Participant;
import com.avenga.a360.model.response.Status;
import com.avenga.a360.model.response.StatusMessage;
import com.avenga.a360.service.AnswerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/answers")
public class AnswerResource {
    @Inject
    AnswerService answerService;

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAnswer(final List<AnswerDto> answersDto) {
       List<Status> statusList =answerService.createAnswersDto(answersDto);
       return Response.status(Response.Status.OK).entity(statusList).build()  ;

    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAnswers() {
        List<AnswerDto> answers = answerService.findAllAnswersDto();
        if (!answers.isEmpty()) {
            return Response.status(Response.Status.OK).entity(answers).build();
        } else
            return Response.status(Response.Status.BAD_REQUEST).entity(new Status("fail", List.of(new StatusMessage("no answers found")))).build();
    }
}




