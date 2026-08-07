package com.cognizant.uams.repository;

import com.cognizant.uams.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
    // Custom Method: Find all grades given in a specific course
    List<Grade> findByCourseId(Integer courseId);
    List<Grade> findByStudentId(Integer studentId);
    java.util.Optional<Grade> findByStudentIdAndCourseId(Integer studentId, Integer courseId);
    void deleteByStudentId(Integer studentId);
    void deleteByCourseId(Integer courseId);
}
