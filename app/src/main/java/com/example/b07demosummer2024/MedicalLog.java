package com.example.b07demosummer2024;

import java.time.LocalDateTime;

public class MedicalLog extends GeneralLog{

    private final Medicine linkedMedicine;

    private final int reflection;
    private final int puffsUsed;

    public MedicalLog(int logId, int logType, LocalDateTime logDate, String descriptions,
                      Medicine linkedMedicine, int reflection, int puffsUsed)
    {
        super(logId, logType, logDate, descriptions);
        this.linkedMedicine = linkedMedicine;
        this.reflection = reflection;
        this.puffsUsed =puffsUsed;
    }

    public int getPuff()
    {
        return puffsUsed;
    }

    public String getReflection()
    {
        if (reflection == 0)
        {
            return "Better";
        } else if (reflection == 1)
        {
            return "Same";
        }
        else {
            return "Worse";
        }

    }

    public Medicine getLinkedMedicine()
    {
        return linkedMedicine;
    }

    public int getLinkedMedicineType()
    {
        if (linkedMedicine != null)
        {
            return linkedMedicine.getMedicineType();
        }
        return -1;
    }

    public int getLinkedMedicineId()
    {
        if (linkedMedicine != null)
        {
            return linkedMedicine.getMedicineId();
        }
        return -1;
    }


}
