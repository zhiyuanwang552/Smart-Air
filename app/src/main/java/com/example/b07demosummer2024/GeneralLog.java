package com.example.b07demosummer2024;

import java.time.LocalDateTime;
public class GeneralLog
{
    private final int logId;
    private final LocalDateTime logDate;
    private final String descriptions;

    public GeneralLog(int logId, LocalDateTime logDate, String descriptions) {
        this.logId = logId;
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
}
