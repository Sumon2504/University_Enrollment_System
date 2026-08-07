package com.cognizant.uams.repository;

import com.cognizant.uams.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByEmailIgnoreCase(String email);
    List<Student> findByNameContainingIgnoreCase(String name);
}
