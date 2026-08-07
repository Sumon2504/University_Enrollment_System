package com.cognizant.uams.dto;

import com.cognizant.uams.entity.Student;
import com.cognizant.uams.enums.Department;

public record StudentAccountView(
        Integer studentId,
        String username,
        String fullName,
        String email,
        Department department,
        String contactNumber,
        Integer enrollmentYear) {

    public static StudentAccountView from(Student student, String username) {
        return new StudentAccountView(
                student.getStudentId(),
                username,
                student.getName(),
                student.getEmail(),
                student.getDepartment(),
                student.getContactNumber(),
                student.getEnrollmentYear());
    }
}
