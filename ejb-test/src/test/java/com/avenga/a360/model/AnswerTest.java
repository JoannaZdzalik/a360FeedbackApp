package com.avenga.a360.model;

import com.avenga.a360.domain.model.Answer;
import com.avenga.a360.domain.model.Participant;
import com.avenga.a360.domain.model.Session;
import org.junit.Test;

import javax.persistence.Query;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class AnswerTest extends JpaTest {

    @Test
    public void shouldFindAnswerListByParticipantId(){

        Participant participant = new Participant();
        participant.setId(1L);

        Query query = em.createNamedQuery("getAllAnswersByParticipantId");
        List <Answer> result = query.setParameter("id", participant ).getResultList();

        assertTrue(result.size()== 5);

    }
}