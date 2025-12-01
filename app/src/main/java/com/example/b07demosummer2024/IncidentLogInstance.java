package com.example.b07demosummer2024;

public class IncidentLogInstance {

    private long timeStamp;
    private String userResponse;
    private String guidanceShown;
    private String pefEntry;
    private Boolean speakingToggle;
    private Boolean breathingToggle;
    private Boolean chestRetractionToggle;
    private Boolean chestPainToggle;
    private Boolean blueSkinToggle;

    public IncidentLogInstance() {}

    public IncidentLogInstance(long timeStamp, String userResponse, String guidanceShown, String pefEntry, Boolean speakingToggle, Boolean breathingToggle, Boolean chestRetractionToggle, Boolean chestPainToggle, Boolean blueSkinToggle) {
        this.timeStamp = timeStamp;
        this.userResponse = userResponse;
        this.guidanceShown = guidanceShown;
        this.pefEntry = pefEntry;
        this.speakingToggle = speakingToggle;
        this.breathingToggle = breathingToggle;
        this.chestRetractionToggle = chestRetractionToggle;
        this.chestPainToggle = chestPainToggle;
        this.blueSkinToggle = blueSkinToggle;

    }

    // Getters and Setters

    public long getTimeStamp() { return timeStamp; }
    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }
    public String getUserResponse() { return userResponse; }
    public void setUserResponse(String userResponse) { this.userResponse = userResponse; }

    public String getGuidanceShown() { return guidanceShown; }
    public void setGuidanceShown(String guidanceShown) { this.guidanceShown = guidanceShown; }

    public String getPefEntry() { return pefEntry; }
    public void setPefEntry(String pefEntry) { this.pefEntry = pefEntry; }

    public Boolean getSpeakingToggle() { return speakingToggle; }
    public void setSpeakingToggle(Boolean speakingToggle) { this.speakingToggle = speakingToggle; }

    public Boolean getBreathingToggle() { return breathingToggle; }
    public void setBreathingToggle(Boolean breathingToggle) { this.breathingToggle = breathingToggle; }

    public Boolean getChestRetractionToggle() { return chestRetractionToggle; }
    public void setChestRetractionToggle(Boolean chestRetractionToggle) { this.chestRetractionToggle = chestRetractionToggle; }

    public Boolean getChestPainToggle() { return chestPainToggle; }
    public void setChestPainToggle(Boolean chestPainToggle) { this.chestPainToggle = chestPainToggle; }

    public Boolean getBlueSkinToggle() { return blueSkinToggle; }
    public void setBlueSkinToggle(Boolean blueSkinToggle) { this.blueSkinToggle = blueSkinToggle; }


}
