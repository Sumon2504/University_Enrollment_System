package com.cognizant.uams;

import com.cognizant.uams.controller.AuthController;
import com.cognizant.uams.entity.Student;
import com.cognizant.uams.entity.User;
import com.cognizant.uams.entity.Faculty;
import com.cognizant.uams.entity.Course;
import com.cognizant.uams.enums.Department;
import com.cognizant.uams.enums.Role;
import com.cognizant.uams.repository.CourseRepository;
import com.cognizant.uams.repository.FacultyRepository;
import com.cognizant.uams.repository.StudentRepository;
import com.cognizant.uams.repository.UserRepository;
import com.cognizant.uams.repository.EnrollmentRepository;
import com.cognizant.uams.repository.GradeRepository;
import com.cognizant.uams.repository.AcademicRecordRepository;
import com.cognizant.uams.security.TokenService;
import com.cognizant.uams.service.FacultyService;
import com.cognizant.uams.service.CourseService;
import com.cognizant.uams.service.EnrollmentService;
import com.cognizant.uams.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerUnitTests {
    @Mock UserRepository userRepository;
    @Mock StudentRepository studentRepository;
    @Mock FacultyRepository facultyRepository;
    @Mock CourseRepository courseRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenService tokenService;
    @Mock EnrollmentRepository enrollmentRepository;
    @Mock GradeRepository gradeRepository;
    @Mock AcademicRecordRepository academicRecordRepository;
    @Mock StudentService studentService;
    @Mock CourseService courseService;

    @Test
    void loginReturnsSessionForValidCredentials() {
        User user = user("admin", Role.ADMIN);
        when(userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase("admin", "admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", user.getPassword())).thenReturn(true);
        when(tokenService.issue(user)).thenReturn("session-token");

        ResponseEntity<Map<String, Object>> response = authController().login(new AuthController.LoginRequest("admin", "admin123"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("token", "session-token").containsEntry("role", "ADMIN");
    }

    @Test
    void loginRejectsInvalidCredentials() {
        when(userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase("unknown", "unknown")).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = authController().login(new AuthController.LoginRequest("unknown", "wrong12"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("message", "Invalid username/email or password");
        verifyNoInteractions(tokenService);
    }

    @Test
    void adminCreatesStudentAndAssignedLogin() {
        when(userRepository.existsByEmailIgnoreCase("new.student@university.edu")).thenReturn(false);
        when(studentRepository.findByEmailIgnoreCase("new.student@university.edu")).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student student = invocation.getArgument(0); student.setStudentId(42); return student;
        });
        when(passwordEncoder.encode("student12")).thenReturn("encoded-password");

        Student student = student("New Student", "new.student@university.edu");
        StudentService service = studentAccountService();
        Student saved = service.create(student, "student12");

        assertThat(service.usernameFor(saved)).isEqualTo("S042");
        assertThat(saved.getStudentId()).isEqualTo(42);
        verify(userRepository).save(argThat(user -> user.getRole() == Role.STUDENT
                && user.getUsername().equals("S042") && user.getPassword().equals("encoded-password")));
    }

    @Test
    void adminRejectsDuplicateStudentEmail() {
        when(userRepository.existsByEmailIgnoreCase("existing@university.edu")).thenReturn(true);

        Student student = student("Existing Student", "existing@university.edu");

        assertThatThrownBy(() -> studentAccountService().create(student, "student12"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already registered");
        verify(studentRepository, never()).save(any());
    }

    @Test
    void facultyAccountUsesAdministratorSelectedPassword() {
        Faculty faculty = new Faculty();
        faculty.setName("Dr Test"); faculty.setEmail("dr.test@university.edu");
        faculty.setDepartment(Department.PHYSICS); faculty.setDesignation("Professor"); faculty.setContactNumber("9876543210");
        when(facultyRepository.findByEmailIgnoreCase("dr.test@university.edu")).thenReturn(Optional.empty());
        when(facultyRepository.save(faculty)).thenAnswer(invocation -> { faculty.setFacultyId(7); return faculty; });
        when(userRepository.existsByEmailIgnoreCase("dr.test@university.edu")).thenReturn(false);
        when(passwordEncoder.encode("chosen12")).thenReturn("encoded-chosen-password");

        Faculty saved = new FacultyService(facultyRepository, userRepository, passwordEncoder).create(faculty, "chosen12");

        assertThat(saved.getFacultyId()).isEqualTo(7);
        verify(passwordEncoder).encode("chosen12");
        verify(userRepository).save(argThat(user -> user.getUsername().equals("F007")
                && user.getPassword().equals("encoded-chosen-password")));
    }

    @Test
    void enrollmentRejectsCourseWhenSeatLimitIsReached() {
        Student student = new Student(); student.setStudentId(9);
        Course course = new Course(); course.setCourseId(4); course.setSeats(1);
        when(studentService.getStudentDetails(9)).thenReturn(student);
        when(courseService.getCourseDetails(4)).thenReturn(course);
        when(enrollmentRepository.findByStudentIdAndCourseId(9, 4)).thenReturn(Optional.empty());
        when(enrollmentRepository.countByCourseIdAndEnrollmentStatus(4, com.cognizant.uams.enums.EnrollmentStatus.ENROLLED)).thenReturn(1L);

        EnrollmentService service = new EnrollmentService(enrollmentRepository, studentService, courseService);

        assertThatThrownBy(() -> service.enrollCourse(9, 4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Course has no available seats");
        verify(enrollmentRepository, never()).save(any());
    }

    private AuthController authController() {
        return new AuthController(userRepository, passwordEncoder, tokenService);
    }

    private StudentService studentAccountService() {
        return new StudentService(studentRepository, userRepository, enrollmentRepository, gradeRepository,
                academicRecordRepository, passwordEncoder);
    }

    private Student student(String name, String email) {
        Student student = new Student();
        student.setName(name);
        student.setEmail(email);
        student.setDepartment(Department.COMPUTER_SCIENCE);
        student.setContactNumber("9876543210");
        student.setEnrollmentYear(2026);
        return student;
    }

    private User user(String username, Role role) {
        User user = new User();
        user.setUserId(1); user.setUsername(username); user.setEmail(username + "@university.edu");
        user.setPassword("encoded"); user.setFullName("Test User"); user.setRole(role);
        return user;
    }
}
