package com.Webapp.repository;

import com.Webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Ensure this import is present

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a User by their name.
     *
     * @param name The name of the user to search for
     * @return An Optional containing the User if found, or an empty Optional if not
     *         found
     */
    Optional<User> findByName(String name);
}
