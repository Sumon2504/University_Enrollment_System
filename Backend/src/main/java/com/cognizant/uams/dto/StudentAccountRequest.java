package com.cognizant.uams.dto;

import com.cognizant.uams.enums.Department;
import jakarta.validation.constraints.*;

public record StudentAccountRequest(
        @NotBlank @Size(min = 2, max = 80) String fullName,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(min = 6, max = 72) String password,
        @NotNull Department department,
        @NotBlank @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "must be a valid phone number") String contactNumber,
        @NotNull @Min(2000) @Max(2100) Integer enrollmentYear) {

}