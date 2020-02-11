package com.avenga.a360.dao.impl;

import com.avenga.a360.dao.AnswerDao;
import com.avenga.a360.domain.model.Answer;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;


public class AnswerDaoImpl implements AnswerDao {

    @PersistenceContext(unitName = "a360")
    private EntityManager em;


    @Override
    public void save(Answer answer) {
        try {
            em.persist(answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Answer> getAllAnswersByParticipantId(Long id) {
                List<Answer> answers = em.createNamedQuery("getAllAnswersByParticipantId", Answer.class)
                        .setParameter("idParticipant", id )
                .getResultList();
        return answers;
    }

    @Override
    public List<Answer> getAllAnswersByParticipantIdAndQuestionId(Long idParticipant, Long idQuestion) {
                 List<Answer> answers = em.createNamedQuery("getAnswersByParticipantIdAndQuestionId", Answer.class)
                         .setParameter("idParticipant", idParticipant)
                         .setParameter("idQuestion", idQuestion)
                .getResultList();
        return answers;
    }
}