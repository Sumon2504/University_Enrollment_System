package com.cognizant.uams.repository;

import com.cognizant.uams.entity.AcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Integer> {
    // Custom Method: Find the complete academic history for a student
    List<AcademicRecord> findByStudentId(Integer studentId);
    Optional<AcademicRecord> findByStudentIdAndCourseId(Integer studentId, Integer courseId);
    void deleteByStudentId(Integer studentId);
    void deleteByCourseId(Integer courseId);
}
