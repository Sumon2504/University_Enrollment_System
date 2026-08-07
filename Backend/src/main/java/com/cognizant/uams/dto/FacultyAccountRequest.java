package com.cognizant.uams.dto;

import com.cognizant.uams.enums.Department;
import jakarta.validation.constraints.*;

public record FacultyAccountRequest(
        @NotBlank @Size(min = 2, max = 80) String name,
        @NotBlank @Email @Size(max = 120) String email,
        @NotNull Department department,
        @NotBlank @Size(min = 2, max = 80) String designation,
        @NotBlank @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "must be a valid phone number") String contactNumber,
        @NotBlank @Size(min = 6, max = 72) String password) {}
