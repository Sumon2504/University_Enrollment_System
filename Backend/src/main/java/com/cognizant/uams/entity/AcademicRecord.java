package com.cognizant.uams.entity;

import jakarta.persistence.*;

@Entity
public class AcademicRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    "We use @GeneratedValue(strategy = GenerationType.IDENTITY) " +
//            "to delegate the Primary Key generation to the underlying database using" +
//            " its auto-increment feature. When we save a new entity, the database " +
//            "automatically assigns a unique sequential ID to that record, " +
//            "preventing manual ID management and primary key conflicts."
    private Integer recordId;
    private Integer studentId;
    private Integer courseId;
    private String grade;
    private String semester;


    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }



}