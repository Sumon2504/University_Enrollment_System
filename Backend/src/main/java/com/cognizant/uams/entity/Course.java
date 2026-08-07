package com.cognizant.uams.entity;

import com.cognizant.uams.enums.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(columnNames = "courseCode"))
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String courseCode;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String courseName;

    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer credits;

    @Enumerated(EnumType.STRING)
//    "We use @Enumerated(EnumType.STRING) to persist the Enum constant's literal name as a String (VARCHAR) " +
//            "in the database rather than its ordinal integer index. By default, JPA uses EnumType.ORDINAL," +
//            " which stores integers (0, 1, 2). However, if someone changes the order of Enum values in code, " +
//            "existing database records become corrupted. " +
//            "Using EnumType.STRING prevents data corruption and makes the database records human-readable.
    @NotNull
    @Column(nullable = false)
    private Department department;

    @NotBlank
    @Column(nullable = false)
    private String semesterOffered;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer seats;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer totalSemesters;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer durationYears;

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getSemesterOffered() {
        return semesterOffered;
    }

    public void setSemesterOffered(String semesterOffered) {
        this.semesterOffered = semesterOffered;
    }

    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }
    public Integer getTotalSemesters() { return totalSemesters; }
    public void setTotalSemesters(Integer totalSemesters) { this.totalSemesters = totalSemesters; }
    public Integer getDurationYears() { return durationYears; }
    public void setDurationYears(Integer durationYears) { this.durationYears = durationYears; }
}
