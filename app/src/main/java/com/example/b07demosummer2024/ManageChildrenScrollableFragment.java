package com.example.b07demosummer2024;

public class ManageChildrenScrollableFragment {

    private String id;
    private String childName;
    private String birthMonth;
    private String birthDay;
    private String birthYear;
    private String notes;
    private String username;
    private String password;
    private String parentId;

    public ManageChildrenScrollableFragment() {}

    public ManageChildrenScrollableFragment(String id, String childName, String birthMonth, String birthDay, String birthYear, String notes, String parentId) {
        this.id = id;
        this.childName = childName;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.birthYear = birthYear;
        this.notes = notes;
        this.parentId = parentId;
    }

    public ManageChildrenScrollableFragment(String childName, String birthMonth, String birthDay, String birthYear, String notes, String username, String password, String parentId) {
        this.childName = childName;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.birthYear = birthYear;
        this.notes = notes;
        this.username = username;
        this.password = password;
        this.parentId = parentId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }
    public String getBirthMonth() { return birthMonth; }
    public void setBirthMonth(String birthMonth) { this.birthMonth = birthMonth; }
    public String getBirthDay() { return birthDay; }
    public void setBirthDay(String birthDay) { this.birthDay = birthDay; }
    public String getBirthYear() { return birthYear; }
    public void setBirthYear(String birthYear) { this.birthYear = birthYear; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
}
