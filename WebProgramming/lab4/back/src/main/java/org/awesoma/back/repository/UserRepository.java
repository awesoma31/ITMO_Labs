package org.awesoma.back.repository;

import org.awesoma.back.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getByUsername(String username);
    User getByUsernameAndPassword(String username, String password);
}