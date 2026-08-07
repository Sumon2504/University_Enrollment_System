package com.cognizant.uams.repository;

import com.cognizant.uams.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findBySemesterOffered(String semesterOffered);
    boolean existsByCourseCodeIgnoreCase(String courseCode);
}
