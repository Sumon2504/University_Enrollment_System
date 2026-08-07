package com.cognizant.uams.controller;

import com.cognizant.uams.entity.Grade;
import com.cognizant.uams.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {
    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public List<Grade> list() { return gradeService.list(); }

    @GetMapping("/course/{courseId}")
    public List<Grade> byCourse(@PathVariable Integer courseId) {

        return gradeService.getGradesByCourse(courseId);
    }

    @GetMapping("/student/{studentId}")
    public List<Grade> byStudent(@PathVariable Integer studentId) {

        return gradeService.getGradesByStudent(studentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Grade submit(@Valid @RequestBody Grade grade) {
        return gradeService.submitGrade(grade);
    }

    @PutMapping("/{gradeId}")
    public Grade update(@PathVariable Integer gradeId, @Valid @RequestBody Grade grade) {
        return gradeService.updateGrade(gradeId, grade);
    }
}
