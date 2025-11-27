package com.example.b07demosummer2024;

import java.time.LocalDateTime;
public abstract class GeneralLog
{
    public static final int MedicalLogType = 0;
    public static final int IncidentLog = 1 ;

    private final int logId;
    private final LocalDateTime logDate;
    private final String descriptions;

    private final int logType;
    public GeneralLog(int logId, int logType, LocalDateTime logDate, String descriptions) {
        this.logId = logId;
        this.logType = logType;
        this.logDate = logDate;
        this.descriptions = descriptions;
    }

    public int getLogId() {
        return logId;
    }

    public LocalDateTime getLogDate() {
        return logDate;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public int getLogType()
    {
        return logType;
    }
}
