package com.cognizant.uams.dto;

import com.cognizant.uams.entity.Faculty;
import com.cognizant.uams.enums.Department;

public record FacultyAccountView(
        Integer facultyId,
        String username,
        String name,
        String email,
        Department department,
        String designation,
        String contactNumber) {

    public static FacultyAccountView from(Faculty faculty, String username) {
        return new FacultyAccountView(
                faculty.getFacultyId(),
                username,
                faculty.getName(),
                faculty.getEmail(),
                faculty.getDepartment(),
                faculty.getDesignation(),
                faculty.getContactNumber());
    }
}
