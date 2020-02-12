package com.avenga.a360.controller;

import com.avenga.a360.dto.QuestionDto;
import com.avenga.a360.model.Question;
import com.avenga.a360.service.QuestionService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/questions")
public class QuestionController {
    @Inject
    QuestionService questionService;

    @Path("/{sessionId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<QuestionDto> getQuestions(@PathParam("sessionId") Long id) {
        List<Question> questionList = questionService.getAllQuestionBySessionId(id);
        List<QuestionDto> questionDtos = questionService.questionListToQuestionDtoList(questionList);
        return questionDtos;
    }
}
