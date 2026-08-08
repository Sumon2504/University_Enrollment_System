package com.cognizant.uams.controller;

import com.cognizant.uams.entity.Enrollment;
import com.cognizant.uams.service.EnrollmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    public record EnrollmentRequest(@NotNull Integer studentId, @NotNull Integer courseId) {}

    @GetMapping
    public List<Enrollment> list(@RequestParam(required = false) Integer studentId,
                                 @RequestParam(required = false) Integer courseId) {
        return enrollmentService.list(studentId, courseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Enrollment enroll(@Valid @RequestBody EnrollmentRequest request) {
        return enrollmentService.enrollCourse(request.studentId(), request.courseId());
    }

    @PutMapping("/{id}/drop")
    public Enrollment drop(@PathVariable Integer id) {
        return enrollmentService.dropCourse(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        enrollmentService.delete(id);
    }
}
