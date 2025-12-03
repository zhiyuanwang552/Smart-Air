package com.example.b07demosummer2024;

public class AlertInstance {

    private long timeStamp;
    private String alertType;
    private String alertSeverity;

    public AlertInstance() {
    }

    public AlertInstance(long timeStamp, String alertType, String alertSeverity) {
        this.timeStamp = timeStamp;
        this.alertType = alertType;
        this.alertSeverity = alertSeverity;

    }

    // Getters and Setters
    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long ts) {
        this.timeStamp = ts;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String at) {
        this.alertType = at;
    }

    public String getAlertSeverity() {
        return alertSeverity;
    }

    public void setAlertSeverity(String as) {
        this.alertSeverity = as;
    }
}
