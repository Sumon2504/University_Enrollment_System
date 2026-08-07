package com.cognizant.uams.service;

import com.cognizant.uams.entity.Course;
import com.cognizant.uams.exception.ResourceNotFoundException;
import com.cognizant.uams.repository.CourseRepository;
import com.cognizant.uams.repository.EnrollmentRepository;
import com.cognizant.uams.repository.GradeRepository;
import com.cognizant.uams.repository.AcademicRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private GradeRepository gradeRepository;
    @Autowired private AcademicRecordRepository academicRecordRepository;

    // 1. Add a new course
    public Course addCourse(Course course) {
        if (courseRepository.existsByCourseCodeIgnoreCase(course.getCourseCode())) {
            throw new IllegalArgumentException("Course code is already in use");
        }
        return courseRepository.save(course);
    }

    // 2. Get course details
    public Course getCourseDetails(Integer courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
    }

    // 3. Update a course
    public Course updateCourse(Integer courseId, Course updatedCourse) {
        Course existingCourse = getCourseDetails(courseId);

        existingCourse.setCourseName(updatedCourse.getCourseName());
        existingCourse.setCourseCode(updatedCourse.getCourseCode());
        existingCourse.setCredits(updatedCourse.getCredits());
        existingCourse.setDepartment(updatedCourse.getDepartment());
        existingCourse.setSemesterOffered(updatedCourse.getSemesterOffered());
        existingCourse.setSeats(updatedCourse.getSeats());
        existingCourse.setTotalSemesters(updatedCourse.getTotalSemesters());
        existingCourse.setDurationYears(updatedCourse.getDurationYears());

        return courseRepository.save(existingCourse);
    }

    // 4. List all courses for a specific semester
    public List<Course> listCoursesBySemester(String semester) {
        return courseRepository.findBySemesterOffered(semester);
    }

    public List<Course> listCourses() {
        return courseRepository.findAll();
    }

    @Transactional
    public void deleteCourse(Integer courseId) {
        gradeRepository.deleteByCourseId(courseId);
        enrollmentRepository.deleteByCourseId(courseId);
        academicRecordRepository.deleteByCourseId(courseId);
        courseRepository.delete(getCourseDetails(courseId));
    }
}
