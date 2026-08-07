package com.cognizant.uams.repository;

import com.cognizant.uams.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
    Optional<Faculty> findByEmailIgnoreCase(String email);
}
