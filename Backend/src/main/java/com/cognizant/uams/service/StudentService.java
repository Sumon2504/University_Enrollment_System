package com.cognizant.uams.service;

import com.cognizant.uams.entity.Student;
import com.cognizant.uams.entity.User;
import com.cognizant.uams.enums.Role;
import com.cognizant.uams.exception.ResourceNotFoundException;
import com.cognizant.uams.repository.StudentRepository;
import com.cognizant.uams.repository.UserRepository;
import com.cognizant.uams.repository.EnrollmentRepository;
import com.cognizant.uams.repository.GradeRepository;
import com.cognizant.uams.repository.AcademicRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository,
                          EnrollmentRepository enrollmentRepository, GradeRepository gradeRepository,
                          AcademicRecordRepository academicRecordRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
        this.academicRecordRepository = academicRecordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Student create(Student student, String rawPassword) {
        String email = student.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)
                || studentRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        student.setEmail(email);
        Student saved = studentRepository.save(student);

        User user = new User();
        user.setUsername(usernameFor(saved));
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(Role.STUDENT);
        user.setFullName(saved.getName());
        user.setStudent(saved);
        userRepository.save(user);
        return saved;
    }

    public String usernameFor(Student student) {
        return "S" + String.format("%03d", student.getStudentId());
    }

    public Student getStudentDetails(Integer studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }

    @Transactional
    public Student updateProfile(Integer studentId, Student updatedStudent) {
        Student existingStudent = getStudentDetails(studentId);
        userRepository.findByStudentStudentId(studentId).ifPresent(user -> {
            if (!user.getEmail().equalsIgnoreCase(updatedStudent.getEmail())
                    && userRepository.existsByEmailIgnoreCase(updatedStudent.getEmail())) {
                throw new IllegalArgumentException("Email is already registered");
            }
            user.setEmail(updatedStudent.getEmail().toLowerCase());
            user.setFullName(updatedStudent.getName());
            userRepository.save(user);
        });
        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setContactNumber(updatedStudent.getContactNumber());
        existingStudent.setDepartment(updatedStudent.getDepartment());
        existingStudent.setEnrollmentYear(updatedStudent.getEnrollmentYear());

        return studentRepository.save(existingStudent);
    }

    public List<Student> listStudents(String query) {
        if (query == null || query.isBlank()) return studentRepository.findAll();
        try {
            return studentRepository.findById(Integer.valueOf(query))
                    .map(List::of).orElseGet(List::of);
        } catch (NumberFormatException ignored) {
            return studentRepository.findByNameContainingIgnoreCase(query);
        }
    }

    public long countStudents() {
        return studentRepository.count();
    }

    @Transactional
    public void deleteStudent(Integer studentId) {
        Student student = getStudentDetails(studentId);
        gradeRepository.deleteByStudentId(studentId);
        enrollmentRepository.deleteByStudentId(studentId);
        academicRecordRepository.deleteByStudentId(studentId);
        userRepository.findByStudentStudentId(studentId).ifPresent(userRepository::delete);
        studentRepository.delete(student);
    }
}
