package com.example.b07demosummer2024;

import java.util.HashMap;
import java.util.Map;

public class DailyCheckInModel {

    public String uid;
    public String email;
    public String userType;
    public String author;
    public String techniquesUsed;
    public String notes;
    public long timestamp;

    public Map<String, Boolean> symptoms = new HashMap<>();
    public Map<String, Boolean> triggers = new HashMap<>();

    public String getUid() {
        return uid;
    }

    public String getTechniquesUsed() {
        return techniquesUsed;
    }
}
