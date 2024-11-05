package com.Webapp.repository;

import com.Webapp.model.Label;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    /**
     * Find a Label by its name.
     *
     * @param name The name of the label to search for
     * @return An Optional containing the Label if found, or an empty Optional if
     *         not found
     */
    Optional<Label> findByName(String name);
}
