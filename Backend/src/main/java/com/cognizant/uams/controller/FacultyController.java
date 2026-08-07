package com.cognizant.uams.controller;

import com.cognizant.uams.dto.FacultyAccountRequest;
import com.cognizant.uams.dto.FacultyAccountView;
import com.cognizant.uams.entity.Faculty;
import com.cognizant.uams.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }



    @GetMapping
    public List<Faculty> list() { return facultyService.list(); }

    @GetMapping("/{id}")
    public Faculty get(@PathVariable Integer id) { return facultyService.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacultyAccountView create(@Valid @RequestBody FacultyAccountRequest request) {
        Faculty faculty = new Faculty();
        faculty.setName(request.name().trim());// it remove the white space beside the name
        faculty.setEmail(request.email().trim().toLowerCase());
        faculty.setDepartment(request.department());
        faculty.setDesignation(request.designation().trim());
        faculty.setContactNumber(request.contactNumber().trim());
        Faculty saved = facultyService.create(faculty, request.password());
        return FacultyAccountView.from(saved, facultyService.usernameFor(saved));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { facultyService.delete(id); }
}
