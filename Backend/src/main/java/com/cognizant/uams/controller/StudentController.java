package com.cognizant.uams.controller;

import com.cognizant.uams.dto.StudentAccountRequest;
import com.cognizant.uams.dto.StudentAccountView;
import com.cognizant.uams.entity.Student;
import com.cognizant.uams.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> list(@RequestParam(required = false) String query) { // here wu used false because if client doesn;t give nay query still it gives the students details
        return studentService.listStudents(query);
    }

    @GetMapping("/{id}")
    public Student get(@PathVariable Integer id) {
        return studentService.getStudentDetails(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentAccountView create(@Valid @RequestBody StudentAccountRequest request) {
        Student student = new Student();
        student.setName(request.fullName().trim());
        student.setEmail(request.email().trim().toLowerCase());
        student.setDepartment(request.department());
        student.setContactNumber(request.contactNumber().trim());
        student.setEnrollmentYear(request.enrollmentYear());

        Student saved = studentService.create(student, request.password());
        return StudentAccountView.from(saved, studentService.usernameFor(saved));
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Integer id, @Valid @RequestBody Student student) {
        return studentService.updateProfile(id, student);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        studentService.deleteStudent(id);
    }
}
