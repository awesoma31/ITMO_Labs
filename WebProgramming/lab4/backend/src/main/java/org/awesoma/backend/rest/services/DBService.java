package org.awesoma.backend.rest.services;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.awesoma.backend.db.data.User;
import org.awesoma.backend.db.exceptions.UsernameAlreadyExistsException;

import java.util.List;
import java.util.Optional;

@Stateless
public class DBService {
    @PersistenceContext(unitName = "MyPU")
    private EntityManager em;

    public List<User> getAllUsers() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User getUserById(Long id) {
        return em.find(User.class, id);
    }

    public void deleteUser(User user) {
        em.remove(em.contains(user) ? user : em.merge(user));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createUser(User user) {
        if (userExists(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + user.getUsername());
        }
        em.persist(user);
        em.flush();
    }

    public boolean userExists(String username) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        return !query.getResultList().isEmpty();
    }

    public Optional<User> findByUsername(String username) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);

            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
