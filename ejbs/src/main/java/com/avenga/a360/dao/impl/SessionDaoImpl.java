package com.avenga.a360.dao.impl;


import com.avenga.a360.dao.SessionDao;
import com.avenga.a360.domain.model.Session;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

public class SessionDaoImpl implements SessionDao {

    @PersistenceContext(unitName = "a360")
    private EntityManager em;

    @Override
    public void save(Session session) {
        try {
            em.persist(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Session> getAllSessionsToSend() {
        List<Session> sessions = em.createNamedQuery("findSessionsToSend", Session.class)
                .getResultList();
        return sessions;
    }


}