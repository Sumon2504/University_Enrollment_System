package com.cognizant.uams.service;

import com.cognizant.uams.entity.Course;
import com.cognizant.uams.entity.Enrollment;
import com.cognizant.uams.entity.Student;
import com.cognizant.uams.enums.EnrollmentStatus;
import com.cognizant.uams.exception.ResourceNotFoundException;
import com.cognizant.uams.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, StudentService studentService,
                             CourseService courseService) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    public Enrollment enrollCourse(Integer studentId, Integer courseId) {
        Student student = studentService.getStudentDetails(studentId);
        Course course = courseService.getCourseDetails(courseId);
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseGet(Enrollment::new);

        if (enrollment.getEnrollmentStatus() != EnrollmentStatus.ENROLLED
                && enrollmentRepository.countByCourseIdAndEnrollmentStatus(courseId, EnrollmentStatus.ENROLLED)
                >= course.getSeats()) {
            throw new IllegalArgumentException("Course has no available seats");
        }

        enrollment.setStudentId(student.getStudentId());
        enrollment.setCourseId(course.getCourseId());
        enrollment.setEnrollmentStatus(EnrollmentStatus.ENROLLED);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment dropCourse(Integer enrollmentId) {
        Enrollment enrollment = get(enrollmentId);
        enrollment.setEnrollmentStatus(EnrollmentStatus.DROPPED);
        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrolledCourses(Integer studentId) {
        studentService.getStudentDetails(studentId);
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> list(Integer studentId, Integer courseId) {
        if (studentId != null) return getEnrolledCourses(studentId);
        if (courseId != null) {
            courseService.getCourseDetails(courseId);
            return enrollmentRepository.findByCourseId(courseId);
        }
        return enrollmentRepository.findAll();
    }

    public void delete(Integer enrollmentId) {
        enrollmentRepository.delete(get(enrollmentId));
    }

    private Enrollment get(Integer id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment record not found with ID: " + id));
    }
}
