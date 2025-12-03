package com.example.b07demosummer2024;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class GeneralLog
{
    public static final int MedicalLogType = 0;
    public static final int IncidentLog = 1 ;

    private String logId;
    private long logDate;
    private String descriptions;

    private int logType;

    public GeneralLog(){}

    public GeneralLog(String logId, int logType, long logDate, String descriptions) {
        this.logId = logId;
        this.logType = logType;
        this.logDate = logDate;
        this.descriptions = descriptions;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public long getLogDate() {
        return logDate;
    }

    public void setLogDate(long logDate) {
        this.logDate = logDate;
    }


    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getFormattedDateTime(String formatString)
    {
        if (formatString == null || formatString.isEmpty()) {
            formatString = "MM/dd/yyyy HH:mm";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(formatString, Locale.getDefault());
        return formatter.format(new Date(getLogDate()));
    }

    public int getLogType()
    {
        return logType;
    }

    public void setLogType(int logType)
    {
        this.logType = logType;
    }
}
