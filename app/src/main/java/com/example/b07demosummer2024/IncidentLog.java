package com.example.b07demosummer2024;
import java.time.LocalDateTime;
public class IncidentLog extends GeneralLog{
    public IncidentLog(int logId, int logType, LocalDateTime logDate, String descriptions) {
        super(logId, logType, logDate, descriptions);
    }
}
