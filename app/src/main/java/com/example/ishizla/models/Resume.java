package com.example.ishizla.models;

public class Resume {
    private int id;
    private int userId;
    private String education;
    private String experience;
    private String skills;
    private String about;
    private String createdAt;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName; // Added for easier display in lists

    public Resume() {
    }

    public Resume(int userId, String education, String experience, String skills, String about) {
        this.userId = userId;
        this.education = education;
        this.experience = experience;
        this.skills = skills;
        this.about = about;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

