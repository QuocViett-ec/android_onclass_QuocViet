package com.example.k234112e_hqv;

import java.util.List;

public class SemesterProgram {
    String semesterName;
    List<Subject> subjects;
    String descriptionText;

    public SemesterProgram(String semesterName, List<Subject> subjects, String descriptionText) {
        this.semesterName = semesterName;
        this.subjects = subjects;
        this.descriptionText = descriptionText;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public String getDescriptionText() {
        return descriptionText;
    }
}
