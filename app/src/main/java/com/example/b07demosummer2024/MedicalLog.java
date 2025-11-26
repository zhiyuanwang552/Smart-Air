package com.example.b07demosummer2024;

import java.time.LocalDateTime;

public class MedicalLog extends GeneralLog{

    private final Medicine linkedMedicine;

    private final int reflection;

    public MedicalLog(int logId, LocalDateTime logDate, String descriptions, Medicine linkedMedicine, int reflection)
    {
        super(logId, logDate, descriptions);
        this.linkedMedicine = linkedMedicine;
        this.reflection = reflection;
    }

    public int getReflection()
    {
        return reflection;
    }

    public Medicine getLinkedMedicine()
    {
        return linkedMedicine;
    }

}
