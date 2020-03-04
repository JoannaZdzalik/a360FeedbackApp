package com.avenga.a360.controller;

import com.avenga.a360.dto.QuestionDto;
import com.avenga.a360.dto.QuestionEditDto;
import com.avenga.a360.dto.SessionDto;
import com.avenga.a360.model.Question;
import com.avenga.a360.model.response.Status;
import com.avenga.a360.model.response.StatusMessage;
import com.avenga.a360.service.QuestionService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/questions")
public class QuestionResource {
    @Inject
    QuestionService questionService;


    @Path("/{sessionId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionsBySessionId(@PathParam("sessionId") Long id) {
        List<Question> questionList = questionService.getAllQuestionBySessionId(id);
        List<QuestionDto> questionDtos = questionService.convertQuestionListToQuestionDtoList(questionList);

        if (questionDtos.isEmpty()) {
            questionDtos = null;
        }
        return Application.validator(questionDtos, questionDtos, "Questions not exist.");
    }

    @Path("/active")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllActiveQuestions() {
        List<QuestionDto> questionDtoList = questionService.findAllActiveQuestions();
        if (!questionDtoList.isEmpty()) {
            return Response.status(Response.Status.OK).entity(questionDtoList).build();
        } else return Response.status(Response.Status.BAD_REQUEST).entity(new Status("fail", List.of(new StatusMessage("No active questions found")))).build();
    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllQuestions() {
        List<QuestionDto> questionDtoList = questionService.findAllQuestions();
        if (!questionDtoList.isEmpty()) {
            return Response.status(Response.Status.OK).entity(questionDtoList).build();
        } else return Response.status(Response.Status.BAD_REQUEST).entity(new Status("fail", List.of(new StatusMessage("No active questions found")))).build();
    }

    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createQuestion(QuestionDto questionDto) {
        boolean status = questionService.createQuestion(questionDto);
        if (status) {
            return Response.status(Response.Status.CREATED).entity("Question added").build();
        } else return Response.status(Response.Status.BAD_REQUEST).entity("Question not added").build();
    }

    @Path("/edit")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response editQuestionIsActive(QuestionEditDto questionEditDto) {
        boolean status = questionService.updateQuestion(questionEditDto);
        if (status) {
            return Response.status(Response.Status.CREATED).entity("Question editted").build();
        } else return Response.status(Response.Status.BAD_REQUEST).entity("Question not editted").build();
    }

}



