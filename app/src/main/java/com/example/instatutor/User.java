package com.example.instatutor;

import java.util.ArrayList;

public class User {
    private String fullName;
    private boolean isOnline;
    private boolean isTutor;
    private String userID;
    private final ArrayList<String> subjects;
    private final ArrayList<String> tutoringSubjects;

    public User() {
        this.fullName = "";
        this.isOnline = false;
        this.isTutor = false;
        this.userID = "";
        this.subjects = new ArrayList<>();
        this.tutoringSubjects = new ArrayList<>();
    }

    public User(String fullName, boolean isOnline, boolean isTutor, String userID,
                 ArrayList<String> subjects, ArrayList<String> tutoringSubjects) {
        this.fullName = fullName;
        this.isOnline = isOnline;
        this.isTutor = isTutor;
        this.userID = userID;

        // Registration doesn't ask for subjects
        if (subjects == null) {
            this.subjects = new ArrayList<>();
        } else {
            this.subjects = subjects;
        }

        // Some students may not want to tutor others
        if (tutoringSubjects == null) {
            this.tutoringSubjects = new ArrayList<>();
        } else {
            this.tutoringSubjects = tutoringSubjects;
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {this.fullName = fullName;}

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isTutor() {
        return isTutor;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setTutor(boolean tutor) {
        isTutor = tutor;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public ArrayList<String> getTutoringSubjects() {
        return tutoringSubjects;
    }

    public void addSubject(String subject) {
        this.subjects.add(subject);
    }

    public void addTutoringSubject(String subject) {
        this.tutoringSubjects.add(subject);
    }

    public void removeSubject(String subject) {
        this.subjects.remove(subject);
    }

    public void removeTutoringSubject(String subject) {
        this.tutoringSubjects.remove(subject);
    }
}
