package com.example.b07demosummer2024;

public class MedicalLog extends GeneralLog{

    private String linkedMedicineId;

    private String reflection;
    private int puffsUsed;

    private String userName;

    private String linkedMedicineType;
    private String userId;
    public MedicalLog(){}
    public MedicalLog(String logId, int logType, long logDate, String descriptions,
                      String linkedMedicineId, String reflection, int puffsUsed, String userName,
                      String linkedMedicineType, String userId)
    {
        super(logId, logType, logDate, descriptions);
        this.linkedMedicineId = linkedMedicineId;
        this.reflection = reflection;
        this.puffsUsed =puffsUsed;
        this.userName = userName;
        this.linkedMedicineType = linkedMedicineType;
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public int getPuff()
    {
        return puffsUsed;
    }

    public void setPuff(int puffsUsed)
    {
        this.puffsUsed = puffsUsed;
    }

    public String getReflection()
    {
        return reflection;
    }

    public void setReflection(String reflection)
    {
        this.reflection = reflection;
    }

    public String getLinkedMedicineId()
    {
        return linkedMedicineId;
    }

    public void setLinkedMedicineId(String linkedMedicineId)
    {
        this.linkedMedicineId = linkedMedicineId;
    }
    public String getLinkedMedicineType()
    {
        return linkedMedicineType;
    }

    public void setLinkedMedicineType(String linkedMedicineType)
    {
        this.linkedMedicineType = linkedMedicineType;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

}
