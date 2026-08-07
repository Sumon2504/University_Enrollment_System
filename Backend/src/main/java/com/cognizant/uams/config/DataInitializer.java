package com.cognizant.uams.config;

import com.cognizant.uams.entity.Student;
import com.cognizant.uams.entity.User;
import com.cognizant.uams.entity.Course;
import com.cognizant.uams.entity.Faculty;
import com.cognizant.uams.enums.Department;
import com.cognizant.uams.enums.Role;
import com.cognizant.uams.repository.StudentRepository;
import com.cognizant.uams.repository.UserRepository;
import com.cognizant.uams.repository.CourseRepository;
import com.cognizant.uams.repository.FacultyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, StudentRepository studentRepository,
                                      FacultyRepository facultyRepository, CourseRepository courseRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Create Default Admin
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@university.edu");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setFullName("System Administrator");
                userRepository.save(admin);
            }

            // 2. Create Default Faculty
            if (userRepository.findByUsername("faculty").isEmpty()) {
                Faculty facultyProfile = new Faculty();
                facultyProfile.setName("Dr. Alan Turing");
                facultyProfile.setEmail("faculty@university.edu");
                facultyProfile.setDepartment(Department.COMPUTER_SCIENCE);
                facultyProfile.setDesignation("Professor");
                facultyProfile.setContactNumber("555-0100");
                facultyRepository.save(facultyProfile);

                User faculty = new User();
                faculty.setUsername("faculty");
                faculty.setEmail("faculty@university.edu");
                faculty.setPassword(passwordEncoder.encode("faculty123"));
                faculty.setRole(Role.FACULTY);
                faculty.setFullName("Dr. Alan Turing");
                faculty.setFaculty(facultyProfile);
                userRepository.save(faculty);
            }

            // 3. Create Default Student
            if (userRepository.findByUsername("student").isEmpty()) {
                Student studentProfile = new Student();
                studentProfile.setName("Jane Doe");
                studentProfile.setEmail("jane@university.edu");
                studentProfile.setContactNumber("555-8888");
                studentProfile.setDepartment(Department.valueOf("COMPUTER_SCIENCE"));
                studentProfile.setEnrollmentYear(2026);
                studentRepository.save(studentProfile);

                User studentUser = new User();
                studentUser.setUsername("student");
                studentUser.setEmail("jane@university.edu");
                studentUser.setPassword(passwordEncoder.encode("student123"));
                studentUser.setRole(Role.STUDENT);
                studentUser.setFullName("Jane Doe");
                studentUser.setStudent(studentProfile);
                userRepository.save(studentUser);
            }

            if (courseRepository.count() == 0) {
                Course course = new Course();
                course.setCourseCode("CS101");
                course.setCourseName("Introduction to Computer Science");
                course.setCredits(4);
                course.setDepartment(Department.COMPUTER_SCIENCE);
                course.setSemesterOffered("Fall");
                course.setSeats(40);
                course.setTotalSemesters(8);
                course.setDurationYears(4);
                courseRepository.save(course);

                Course algorithms = new Course();
                algorithms.setCourseCode("CS202");
                algorithms.setCourseName("Data Structures and Algorithms");
                algorithms.setCredits(4);
                algorithms.setDepartment(Department.COMPUTER_SCIENCE);
                algorithms.setSemesterOffered("Spring");
                algorithms.setSeats(35);
                algorithms.setTotalSemesters(8);
                algorithms.setDurationYears(4);
                courseRepository.save(algorithms);
            }
        };
    }
}
