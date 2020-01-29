package com.avenga.a360.service;

import com.avenga.a360.dao.QuestionDao;
import com.avenga.a360.dao.SessionDao;
import com.avenga.a360.domain.dto.ParticipantDto;
import com.avenga.a360.domain.dto.QuestionDto;
import com.avenga.a360.domain.dto.SessionDto;
import com.avenga.a360.domain.model.Participant;
import com.avenga.a360.domain.model.Question;
import com.avenga.a360.domain.model.Session;
import com.avenga.a360.service.impl.SessionServiceImpl;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SessionServiceImplTest {

    @InjectMocks
    SessionServiceImpl sessionServiceImpl;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private QuestionDao questionDao;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void shouldFindSessionsEndedInThePastButNotSent() {
        List<Session> allSessions = new ArrayList<>();
        allSessions.add(new Session(1L, "AvengaFirstEdition", LocalDateTime.of(2019, 10, 10, 12, 00), false, null, null));

        when(sessionDao.getAllSessionsToSend()).thenReturn(allSessions);

        assertEquals(1, sessionServiceImpl.findSessionsEndedInThePastButNotSent().size());
    }


    @Test
    public void shouldSaveCompleteSessionWithCorrectParameters() {
        List<Question> questions = new ArrayList<>();
        Question question1 = new Question();
        question1.setQuestionText("How do you like him?");
        question1.setQuestionType(Question.QuestionType.TEXT);
        question1.setDefaultAnswers(null);
        questions.add(question1);

        List<ParticipantDto> participants = new ArrayList<>();
        ParticipantDto asia = new ParticipantDto();
        asia.setEmail("asia@zdz");
        ParticipantDto kasia = new ParticipantDto();
        kasia.setEmail("kasia@yzdz");
        participants.add(kasia);

        SessionDto newSession = new SessionDto();
        newSession.setName("Avenga First Edition");
        newSession.setEndDate(LocalDateTime.of(2020, 02, 02, 20, 00));

        when(questionDao.getAllActiveQuestions()).thenReturn(questions);

        assertTrue(sessionServiceImpl.createNewSession(newSession, participants));
    }

    @Test
    public void shouldMapSessionDtoToSession() {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("SessionOne");
        sessionDto.setEndDate(LocalDateTime.of(2020, 02, 15, 20, 00, 00));

        Session session = sessionServiceImpl.sessionDtoToSession(sessionDto);

        assertEquals(session.getId(), sessionDto.getId());
    }

    @Test
    public void shouldMapSessionToSessionDto() {
        Session session = new Session();
        session.setId(1L);
        session.setName("Session First Edition");
        session.setEndDate(LocalDateTime.of(2020,03,15,20,00,00));

        SessionDto sessionDto = sessionServiceImpl.sessionToSessionDto(session);

        assertEquals(session.getId(), sessionDto.getId());
        assertEquals(session.getName(), sessionDto.getName());
        assertEquals(session.getEndDate(), sessionDto.getEndDate());
    }

    @Test
    public void shouldNotSaveSessionWhenSetEndDateIsInThePast() {
        List<Question> questions = new ArrayList<>();
        Question question1 = new Question();
        question1.setQuestionText("How do you like him?");
        question1.setQuestionType(Question.QuestionType.TEXT);
        question1.setDefaultAnswers(null);
        questions.add(question1);

        List<ParticipantDto> participants = new ArrayList<>();
        ParticipantDto asia = new ParticipantDto();
        asia.setEmail("asia@zdz");
        ParticipantDto kasia = new ParticipantDto();
        kasia.setEmail("kasia@yzdz");
        participants.add(kasia);

        SessionDto newSession = new SessionDto();
        newSession.setName("Avenga First Edition");
        newSession.setSent(false);
        newSession.setEndDate(LocalDateTime.of(2019, 02, 02, 20, 00));

        when(questionDao.getAllActiveQuestions()).thenReturn(questions);

        assertFalse(sessionServiceImpl.createNewSession(newSession, participants));
    }


    @Test
    public void shouldNotSaveSessionWhenListOfQuestionsIsEmpty() {
        List<ParticipantDto> participants = new ArrayList<>();
        ParticipantDto asia = new ParticipantDto();
        asia.setEmail("asia@zdz");

        SessionDto newSession = new SessionDto();
        newSession.setName("Avenga First Edition");
        newSession.setSent(false);
        newSession.setEndDate(LocalDateTime.of(2020, 02, 02, 20, 00));

        assertFalse(sessionServiceImpl.createNewSession(newSession, participants));

    }

    @Test
    public void shouldNotSaveSessionWhenListOfParticipantsIsEmpty() {
        List<Question> questions = new ArrayList<>();
        Question question1 = new Question();
        question1.setQuestionText("How do you like him?");
        question1.setQuestionType(Question.QuestionType.TEXT);
        question1.setDefaultAnswers(null);
        questions.add(question1);

        List<ParticipantDto> participants = new ArrayList<>();

        SessionDto newSession = new SessionDto();
        newSession.setName("Avenga First Edition");
        newSession.setEndDate(LocalDateTime.of(2020, 02, 02, 20, 00));

        when(questionDao.getAllActiveQuestions()).thenReturn(questions);

        assertFalse(sessionServiceImpl.createNewSession(newSession, participants));
    }


}