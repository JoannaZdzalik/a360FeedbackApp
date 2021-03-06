package com.avenga.a360.service.impl;

import com.avenga.a360.dao.AnswerDao;
import com.avenga.a360.dao.ParticipantDao;
import com.avenga.a360.dao.QuestionDao;
import com.avenga.a360.dao.SessionDao;
import com.avenga.a360.dto.EditDto.SessionEditDto;
import com.avenga.a360.dto.ParticipantDto;
import com.avenga.a360.dto.QuestionDto;
import com.avenga.a360.dto.SessionDto;
import com.avenga.a360.model.Answer;
import com.avenga.a360.model.Participant;
import com.avenga.a360.model.Question;
import com.avenga.a360.model.Session;
import com.avenga.a360.model.response.Status;
import com.avenga.a360.model.response.StatusMessage;
import com.avenga.a360.service.EmailService;
import com.avenga.a360.service.SendService;
import com.avenga.a360.service.SessionService;
import org.jboss.resteasy.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SessionServiceImpl implements SessionService {

    private final static Logger LOGGER = Logger.getLogger(SessionServiceImpl.class);

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Inject
    SessionDao sessionDao;


    @Inject
    ParticipantDao participantDao;

    @Inject
    SendService sendService;

    @Inject
    EmailService emailService;

    @Inject
    AnswerDao answerDao;


    @Override
    public Status createSession(SessionDto sessionDto, List<ParticipantDto> participantsDto, List<QuestionDto> questionsDto) {
        Status status = new Status();
        List<StatusMessage> statusMessages = new ArrayList<>();

        validateIsNotNull(status, sessionDto, statusMessages, "Session object is null");
        validateIsNotNull(status, participantsDto, statusMessages, "Participant object is null");
        validateIsNotNull(status, questionsDto, statusMessages, "Question object is null.");

        if (validateIsNotNull(sessionDto) && validateIsNotNull(participantsDto) && validateIsNotNull(questionsDto)) {
            validateIsNotNull(status, (Integer) participantsDto.size(), statusMessages, "Participant list is empty");

            if (sessionDto.getSessionName() != null && sessionDto.getEndDate() != null &&
                    !(sessionDto.getEndDate().isBefore(LocalDateTime.now())) &&
                    questionsDto.size() != 0 && participantsDto.size() != 0) {
                Session session = sessionDtoToSession(sessionDto);
                session.setParticipants(participantDtoListToParticipantList(participantsDto, session));
                session.setQuestions(questionsDtoListToQuestions(questionsDto, session));
                if (!sessionDao.checkIfSessionNameExistsInDB(session.getSessionName())) {
                    sessionDao.createSession(session);
                    status.setStatus("success");
                    statusMessages.add(new StatusMessage("Session object created"));
                    sendService.sendEmailsToAllParticipants(emailService.createEmailsToParticipantsWithLinks(session.getParticipants(), session));
                    LOGGER.info("Session with name: " + session.getSessionName() + " created");
                } else {
                    status.setStatus("fail");
                    statusMessages.add(new StatusMessage("Session name exists in database"));
                    LOGGER.warn("Session with name: " + session.getSessionName() + " could not be created - name exists in database");
                }
            } else {
                if (sessionDto.getEndDate() == null) {
                    statusMessages.add(new StatusMessage("End date is empty"));
                    LOGGER.warn("Session could not be created - end date is null");
                } else {
                    if (sessionDto.getEndDate().isBefore(LocalDateTime.now())) {
                        statusMessages.add(new StatusMessage("End date is before now"));
                        LOGGER.warn("Session could not be created - end date is before now");
                    }
                }
                if (sessionDto.getSessionName() == null) {
                    statusMessages.add(new StatusMessage("session name is empty"));
                    LOGGER.warn("Session could not be created - session is null");
                }
                status.setStatus("fail");
            }
        }
        status.setStatusMessageList(statusMessages);
        return status;
    }

    @Override
    public void removeSession(Long id) {
        Session sessionToRemove = sessionDao.findSessionById(id);
        if (sessionToRemove != null && !sessionToRemove.isActive()) {
            cleanUpAnswers(sessionToRemove);
            sessionDao.removeSession(sessionToRemove.getId());
        }
    }

    private void cleanUpAnswers(Session sessionToRemove) {
        List<Answer> answers = answerDao.findAllAnswersForOneSession(sessionToRemove.getSessionName());
        for (Answer answer : answers) {
            if (answer != null) {
                answerDao.deleteAnswer(answer);
            }
        }
    }

    @Override
    public boolean updateSession(SessionEditDto sessionEditDto) {
        Session session = sessionDao.findSessionByName(sessionEditDto.getSessionName());
        if (session != null) {
            session.setActive(sessionEditDto.isActive());
            sessionDao.updateSession(session);
            return true;
        }
        return false;
    }

    private boolean validateIsNotNull(Status status, Object o, List<StatusMessage> statusMessageList, String
            message) {
        if (o == null) {
            statusMessageList.add((new StatusMessage(message)));
            status.setStatus("fail");
            return false;
        }
        if (o.equals(0)) {
            statusMessageList.add((new StatusMessage(message)));
            status.setStatus("fail");
            return false;
        }
        return true;
    }

    private boolean validateIsNotNull(Object o) {
        if (o == null) {
            return false;
        }
        return !o.equals(0);
    }

    @Override
    public List<Session> findAllSessionsIsSentFalseAndEndDateIsAfterNow() {
        return sessionDao.findAllSessionsIsSentFalseAndEndDateIsAfterNow();
    }


    @Override
    public SessionDto findSessionByParticipantUid(String uid) {
        return convertSessionToSessionDto(sessionDao.findSessionByParticipantUid(uid));
    }

    @Override
    public List<SessionDto> findAllSessions() {
        return sessionListToSessionDtoList(sessionDao.findAllSessions());
    }

    public Session sessionDtoToSession(SessionDto sessionDto) {
        Session session = new Session();
        session.setSessionName(sessionDto.getSessionName());
        session.setEndDate(sessionDto.getEndDate());
        session.setIsSent(false);
        session.setActive(true);
        return session;
    }

    public SessionDto convertSessionToSessionDto(Session session) {
        ParticipantServiceImpl participantService = new ParticipantServiceImpl();
        QuestionServiceImpl questionService = new QuestionServiceImpl();
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(session.getId());
        sessionDto.setSessionName(session.getSessionName());
        sessionDto.setEndDate(session.getEndDate());
        sessionDto.setIsSent(session.getIsSent());
        sessionDto.setActive(session.isActive());
        List<ParticipantDto> participantsDto = new ArrayList<>();
        for (Participant participant : session.getParticipants()) {
            ParticipantDto participantDto = participantService.participantToParticipantDto(participant);
            participantsDto.add(participantDto);
        }
        sessionDto.setParticipantList(participantsDto);
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for (Question question : session.getQuestions()) {
            QuestionDto questionDto = questionService.convertQuestionToQuestionDto(question);
            questionDtoList.add(questionDto);
        }
        sessionDto.setQuestionList(questionDtoList);

        return sessionDto;
    }

    public List<SessionDto> sessionListToSessionDtoList(List<Session> sessionList) {
        List<SessionDto> sessionDtoList = new ArrayList<>();
        ParticipantServiceImpl participantService = new ParticipantServiceImpl();
     //   QuestionServiceImpl questionService = new QuestionServiceImpl();
        for (Session session : sessionList) {
            SessionDto sessionDto = new SessionDto();
            sessionDto.setId(session.getId());
            sessionDto.setSessionName(session.getSessionName());
            sessionDto.setIsSent(session.getIsSent());
            sessionDto.setEndDate(session.getEndDate());
            sessionDto.setActive(session.isActive());

            List<ParticipantDto> participantDtoList = new ArrayList<>();
            for (Participant participant : session.getParticipants()) {
                ParticipantDto participantDto = participantService.participantToParticipantDto(participant);
                participantDtoList.add(participantDto);
            }
            sessionDto.setParticipantList(participantDtoList);

//            List<QuestionDto> questionDtoList = new ArrayList<>();
//            for (Question question : session.getQuestions()) {
//                QuestionDto questionDto = questionService.convertQuestionToQuestionDto(question);
//                questionDtoList.add(questionDto);
//            }
//            sessionDto.setQuestionList(questionDtoList);

            sessionDtoList.add(sessionDto);
        }
        return sessionDtoList;
    }

    public List<Participant> participantDtoListToParticipantList(List<ParticipantDto> participantsDto, Session
            session) {
        List<Participant> participants = new ArrayList<>();
        for (ParticipantDto participantDto : participantsDto) {
            Participant participant = new Participant();
            participant.setEmail(participantDto.getEmail());
            participant.setSession(session);
            while (true) {
                String generatedUId = generateUIdForParticipant(15);
                if (participantDao.findByUId(generatedUId) == null) {
                    participant.setUId(generatedUId);
                    break;
                }
            }
            participants.add(participant);
        }
        return participants;
    }

    public List<Question> questionsDtoListToQuestions(List<QuestionDto> questionsDto, Session session) {
        List<Question> questions = new ArrayList<>();
        for (QuestionDto questionDto : questionsDto) {
            Question question = new Question();
            question.setQuestionText(questionDto.getQuestion_text());
            question.setQuestionType(questionDto.getQuestion_type());
            question.setIsDefault(questionDto.getIs_default());
            question.setDefaultAnswers(questionDto.getDefault_answers());
            questions.add(question);
        }
        return questions;
    }

    public String generateUIdForParticipant(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}