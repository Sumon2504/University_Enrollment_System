package com.cognizant.uams.service;

import com.cognizant.uams.entity.Faculty;
import com.cognizant.uams.entity.User;
import com.cognizant.uams.enums.Role;
import com.cognizant.uams.exception.ResourceNotFoundException;
import com.cognizant.uams.repository.FacultyRepository;
import com.cognizant.uams.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public FacultyService(FacultyRepository facultyRepository, UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.facultyRepository = facultyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Faculty> list() { return facultyRepository.findAll(); }

    public Faculty get(Integer id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty member not found with ID: " + id));
    }

    @Transactional
    public Faculty create(Faculty faculty, String rawPassword) {
        facultyRepository.findByEmailIgnoreCase(faculty.getEmail()).ifPresent(existing -> {
            throw new IllegalArgumentException("Faculty email is already registered");
        });
        Faculty saved = facultyRepository.save(faculty);
        String username = usernameFor(saved);
        if (userRepository.existsByEmailIgnoreCase(saved.getEmail())) {
            throw new IllegalArgumentException("Email is already associated with an account");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(saved.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(Role.FACULTY);
        user.setFullName(saved.getName());
        user.setFaculty(saved);
        userRepository.save(user);
        return saved;
    }

    public String usernameFor(Faculty faculty) {
        return "F" + String.format("%03d", faculty.getFacultyId());
    }

    @Transactional
    public void delete(Integer id) {
        Faculty faculty = get(id);
        userRepository.findByFacultyFacultyId(id).ifPresent(userRepository::delete);
        facultyRepository.delete(faculty);
    }
}
