package com.cognizant.uams.service;

import com.cognizant.uams.entity.AcademicRecord;
import com.cognizant.uams.entity.Course;
import com.cognizant.uams.entity.Grade;
import com.cognizant.uams.exception.ResourceNotFoundException;
import com.cognizant.uams.repository.AcademicRecordRepository;
import com.cognizant.uams.repository.GradeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeService {
    private final GradeRepository gradeRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final StudentService studentService;
    private final CourseService courseService;

    public GradeService(GradeRepository gradeRepository, AcademicRecordRepository academicRecordRepository,
                        StudentService studentService, CourseService courseService) {
        this.gradeRepository = gradeRepository;
        this.academicRecordRepository = academicRecordRepository;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @Transactional
    public Grade submitGrade(Grade grade) {
        studentService.getStudentDetails(grade.getStudentId());
        Course course = courseService.getCourseDetails(grade.getCourseId());
        Grade saved = gradeRepository.findByStudentIdAndCourseId(grade.getStudentId(), grade.getCourseId())
                .orElse(grade);
        saved.setGrade(grade.getGrade().toUpperCase());
        saved.setRemarks(grade.getRemarks());
        saved.setStudentId(grade.getStudentId());
        saved.setCourseId(grade.getCourseId());
        syncRecord(gradeRepository.save(saved), course);
        return saved;
    }

    @Transactional
    public Grade updateGrade(Integer gradeId, Grade updatedGrade) {
        Grade existing = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade record not found with ID: " + gradeId));
        existing.setGrade(updatedGrade.getGrade().toUpperCase());
        existing.setRemarks(updatedGrade.getRemarks());
        Course course = courseService.getCourseDetails(existing.getCourseId());
        syncRecord(gradeRepository.save(existing), course);
        return existing;
    }

    public List<Grade> getGradesByCourse(Integer courseId) {
        courseService.getCourseDetails(courseId);
        return gradeRepository.findByCourseId(courseId);
    }

    public List<Grade> getGradesByStudent(Integer studentId) {
        studentService.getStudentDetails(studentId);
        return gradeRepository.findByStudentId(studentId);
    }

    public List<Grade> list() { return gradeRepository.findAll(); }

    private void syncRecord(Grade grade, Course course) {
        AcademicRecord record = academicRecordRepository
                .findByStudentIdAndCourseId(grade.getStudentId(), grade.getCourseId())
                .orElseGet(AcademicRecord::new);
        record.setStudentId(grade.getStudentId());
        record.setCourseId(grade.getCourseId());
        record.setGrade(grade.getGrade());
        record.setSemester(course.getSemesterOffered());
        academicRecordRepository.save(record);
    }
}
