package com.cognizant.uams.repository;

import com.cognizant.uams.entity.Enrollment;
import com.cognizant.uams.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    // Custom Method: Find all enrollments for a specific student
    List<Enrollment> findByStudentId(Integer studentId);
    List<Enrollment> findByCourseId(Integer courseId);
    Optional<Enrollment> findByStudentIdAndCourseId(Integer studentId, Integer courseId);
    long countByCourseIdAndEnrollmentStatus(Integer courseId, EnrollmentStatus enrollmentStatus);
    void deleteByStudentId(Integer studentId);
    void deleteByCourseId(Integer courseId);
}
