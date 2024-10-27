package com.Webapp.repository;

import com.Webapp.model.Label;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long>{
    Optional<Label> findByName(String name);
}
