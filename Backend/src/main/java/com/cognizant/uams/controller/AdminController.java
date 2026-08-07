package com.cognizant.uams.controller;

import com.cognizant.uams.entity.User;
import com.cognizant.uams.enums.Role;
import com.cognizant.uams.exception.ResourceNotFoundException;
import com.cognizant.uams.repository.CourseRepository;
import com.cognizant.uams.repository.FacultyRepository;
import com.cognizant.uams.repository.UserRepository;
import com.cognizant.uams.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final StudentService studentService;

    public AdminController(UserRepository userRepository, FacultyRepository facultyRepository,
                           CourseRepository courseRepository, StudentService studentService) {
        this.userRepository = userRepository;
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.studentService = studentService;
    }

    public record RoleRequest(Role role) {}

    public record UserView(Integer userId, String username, String fullName, String email, Role role) {
        static UserView from(User user) {
            return new UserView(user.getUserId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getRole());
        }
    }

    @GetMapping("/users")
    public List<UserView> users() {
        return userRepository.findAll().stream().map(UserView::from).toList();
    }

    @PutMapping("/users/{id}/role")
    public UserView updateRole(@PathVariable Integer id, @RequestBody RoleRequest request) {
        if (request.role() == null) throw new IllegalArgumentException("Role is required");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setRole(request.role());
        return UserView.from(userRepository.save(user));
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of(
                "faculty", facultyRepository.count(),
                "courses", courseRepository.count(),
                "students", studentService.countStudents(),
                "users", userRepository.count()
        );
    }
}
