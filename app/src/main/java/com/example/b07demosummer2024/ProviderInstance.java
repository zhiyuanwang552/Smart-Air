package com.example.b07demosummer2024;

public class ProviderInstance {

    private String id;
    private String providerName;
    private String notes;
    private Boolean rescueToggle;
    private Boolean controllerToggle;
    private Boolean symptomToggle;
    private Boolean triggersToggle;
    private Boolean pefToggle;
    private Boolean triageToggle;
    private Boolean summaryToggle;

    public ProviderInstance() {}

    public ProviderInstance(String id, String providerName) {
        this.id = id;
        this.providerName = providerName;
        // Boolean toggles initialize as false.

    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Boolean getRescueToggle() { return rescueToggle; }
    public void setRescueToggle(Boolean toggle) { this.rescueToggle = toggle; }
    public Boolean getControllerToggle() { return controllerToggle; }
    public void setControllerToggle(Boolean toggle) { this.controllerToggle = toggle; }
    public Boolean getSymptomToggle() { return symptomToggle; }
    public void setSymptomToggle(Boolean toggle) { this.symptomToggle = toggle; }
    public Boolean getTriggersToggle() { return triggersToggle; }
    public void setTriggersToggle(Boolean toggle) { this.triggersToggle = toggle; }
    public Boolean getPefToggle() { return pefToggle; }
    public void setPefToggle(Boolean toggle) { this.pefToggle = toggle; }
    public Boolean getTriageToggle() { return triageToggle; }
    public void setTriageToggle(Boolean toggle) { this.triageToggle = toggle; }
    public Boolean getSummaryToggle() { return summaryToggle; }
    public void setSummaryToggle(Boolean toggle) { this.summaryToggle = toggle; }


}
