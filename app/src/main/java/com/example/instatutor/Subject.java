package com.example.instatutor;

import java.util.ArrayList;

public class Subject {
    private String subjectName;
    private ArrayList<String> subtopics;

    public Subject() {
        this.subtopics = new ArrayList<>();
        this.subjectName = "";
    }

    public Subject(ArrayList<String> subtopics, String subjectName) {
        this.subtopics = subtopics;
        this.subjectName = subjectName;
    }

    public ArrayList<String> getSubtopics() {
        return subtopics;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubtopics(ArrayList<String> subtopics) {
        this.subtopics = subtopics;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
