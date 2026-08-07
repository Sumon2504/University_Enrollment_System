package com.cognizant.uams;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiWorkflowIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void completeCrossRoleWorkflowPersistsAndReturnsMappedFields() throws Exception {
        String adminToken = login("admin", "admin123", "ADMIN").get("token").asText();

        JsonNode course = json(mvc.perform(post("/api/courses")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseCode":"IT999","courseName":"Integration Engineering","credits":4,
                                 "department":"INFORMATION_TECHNOLOGY","semesterOffered":"Fall 2026",
                                 "seats":25,"totalSemesters":8,"durationYears":4}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseCode").value("IT999"))
                .andExpect(jsonPath("$.seats").value(25))
                .andReturn().getResponse().getContentAsString());
        int courseId = course.get("courseId").asInt();

        JsonNode faculty = json(mvc.perform(post("/api/faculty")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {"name":"Dr Integration","email":"integration.faculty@university.edu",
                                  "department":"INFORMATION_TECHNOLOGY","designation":"Professor",
                                  "contactNumber":"555-9000","password":"faculty12"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.designation").value("Professor"))
                .andReturn().getResponse().getContentAsString());
        assertThat(faculty.get("facultyId").asInt()).isPositive();

        JsonNode studentAccount = json(mvc.perform(post("/api/students")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"API Student","email":"api.student@university.edu","password":"secret12",
                                 "department":"INFORMATION_TECHNOLOGY","contactNumber":"9876543210","enrollmentYear":2026}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").exists())
                .andReturn().getResponse().getContentAsString());
        JsonNode studentSession = login(studentAccount.get("username").asText(), "secret12", "STUDENT");
        String studentToken = studentSession.get("token").asText();
        int studentId = studentSession.get("studentId").asInt();

        mvc.perform(post("/api/enrollments")
                        .header("Authorization", bearer(studentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":" + studentId + ",\"courseId\":" + courseId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enrollmentStatus").value("ENROLLED"));

        String facultyToken = login(faculty.get("username").asText(), "faculty12", "FACULTY").get("token").asText();
        mvc.perform(post("/api/grades")
                        .header("Authorization", bearer(facultyToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"studentId\":" + studentId + ",\"courseId\":" + courseId +
                                ",\"grade\":\"A\",\"remarks\":\"Excellent\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.grade").value("A"));

        mvc.perform(get("/api/grades/student/{id}", studentId)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(courseId))
                .andExpect(jsonPath("$[0].remarks").value("Excellent"));

        MvcResult pdf = mvc.perform(get("/api/transcripts/{id}/pdf", studentId)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andReturn();
        assertThat(new String(pdf.getResponse().getContentAsByteArray(), 0, 8, StandardCharsets.US_ASCII))
                .isEqualTo("%PDF-1.4");

        mvc.perform(get("/api/admin/stats").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students").isNumber())
                .andExpect(jsonPath("$.faculty").isNumber());
    }

    @Test
    void validationAndAuthorizationReturnExpectedStatuses() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"student\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());

        mvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"\",\"email\":\"invalid\",\"password\":\"x\"}"))
                .andExpect(status().isForbidden());

        String studentToken = login("student", "student123", "STUDENT").get("token").asText();
        String adminToken = login("admin", "admin123", "ADMIN").get("token").asText();
        mvc.perform(post("/api/students")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"\",\"email\":\"invalid\",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        mvc.perform(post("/api/courses")
                        .header("Authorization", bearer(studentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseCode":"NOPE","courseName":"Forbidden","credits":3}
                                """))
                .andExpect(status().isForbidden());

        mvc.perform(get("/api/students/999999").header("Authorization", bearer(studentToken)))
                .andExpect(status().isNotFound());
    }

    private JsonNode login(String username, String password, String expectedRole) throws Exception {
        String body = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value(expectedRole))
                .andReturn().getResponse().getContentAsString();
        return json(body);
    }

    private JsonNode json(String content) throws Exception {
        return mapper.readTree(content);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
