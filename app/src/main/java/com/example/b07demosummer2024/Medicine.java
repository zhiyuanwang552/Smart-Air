package com.example.b07demosummer2024;

import androidx.annotation.Nullable;
import java.time.LocalDate;


public class Medicine {
    private int medicineId;
    private long expireDate;
    private long purchaseDate;
    private String medicineType;
    private int remainingPuffs;
    private double price;

    private String brandName;

    Medicine (int medicineId, long expireDate, long purchaseDate, String medicineType, int remainingPuffs, double price, @Nullable String brandName) {

        this.medicineId = medicineId;
        this.expireDate = expireDate;
        this.purchaseDate = purchaseDate;
        this.medicineType = medicineType;
        this.remainingPuffs = remainingPuffs;
        this.price = price;
        this.brandName = brandName;
    }

    public long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(long expireDate)
    {
        this.expireDate = expireDate;
    }

    public long getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(long purchaseDate)
    {
        this.purchaseDate = purchaseDate;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType)
    {
        this.medicineType = medicineType;
    }

    public int getRemainingPuffs(){
        return remainingPuffs;
    }

    public void setRemainingPuffs(int remainingPuffs) {
        this.remainingPuffs = remainingPuffs;
    }
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName)
    {
        this.brandName = brandName;
    }
    public int getMedicineId()
    {
        return medicineId;
    }

    public void setMedicineId(int medicineId)
    {
        this.medicineId = medicineId;
    }
}
