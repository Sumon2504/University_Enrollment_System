package com.cognizant.uams.entity;

import com.cognizant.uams.enums.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "students", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;

    @NotBlank
    @Size(min = 2, max = 80)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Department department;

    @NotBlank
    @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "must be a valid phone number")
    @Column(nullable = false)
    private String contactNumber;

    @NotNull
    @Min(2000)
    @Max(2100)
    @Column(nullable = false)
    private Integer enrollmentYear;

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }
}
