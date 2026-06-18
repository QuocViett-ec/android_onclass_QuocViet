package com.example.k234112e_hqv;

import java.util.List;

public class TrainingProgram {
    String programName;
    String url;
    List<String> keywords;
    List<SemesterProgram> semesters;

    public TrainingProgram(String programName, String url, List<String> keywords, List<SemesterProgram> semesters) {
        this.programName = programName;
        this.url = url;
        this.keywords = keywords;
        this.semesters = semesters;
    }

    public String getProgramName() {
        return programName;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<SemesterProgram> getSemesters() {
        return semesters;
    }
}
