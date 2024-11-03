package org.awesoma.backend.rest.services;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.awesoma.backend.db.data.Result;
import org.awesoma.backend.db.data.User;

import java.util.List;

@Stateless
public class ResultService {

    @PersistenceContext(unitName = "MyPU")
    private EntityManager em;

    public void createResult(Result result) {
        em.persist(result);
    }

    public List<Result> getResultsForUser(User owner) {
        return em.createQuery("SELECT r FROM Result r WHERE r.owner = :owner", Result.class)
                .setParameter("owner", owner)
                .getResultList();
    }

    public void deleteResult(Result result) {
        em.remove(em.contains(result) ? result : em.merge(result));
    }
}
