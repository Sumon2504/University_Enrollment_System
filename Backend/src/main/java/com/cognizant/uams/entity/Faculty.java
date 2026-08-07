package com.cognizant.uams.entity;

import com.cognizant.uams.enums.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "faculty", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer facultyId;

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
    @Size(min = 2, max = 80)
    @Column(nullable = false)
    private String designation;

    @NotBlank
    @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "must be a valid phone number")
    @Column(nullable = false)
    private String contactNumber;

    public Integer getFacultyId() { return facultyId; }
    public void setFacultyId(Integer facultyId) { this.facultyId = facultyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}
