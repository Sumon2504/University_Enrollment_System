package com.cognizant.uams.controller;

import com.cognizant.uams.entity.Course;
import com.cognizant.uams.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> listCourses() {
        return courseService.listCourses();
    }

    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Integer id) {
        return courseService.getCourseDetails(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Course addCourse(@Valid @RequestBody Course course) {
        return courseService.addCourse(course);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Integer id, @Valid @RequestBody Course course) {
        return courseService.updateCourse(id, course);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
    }

    @GetMapping("/semester/{semester}")
    public List<Course> bySemester(@PathVariable String semester) {
        return courseService.listCoursesBySemester(semester);
    }
}
