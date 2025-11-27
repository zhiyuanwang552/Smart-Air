package com.example.b07demosummer2024;

import androidx.annotation.Nullable;
import java.time.LocalDate;


public class Medicine {
    private final int medicineId;
    private final LocalDate expireDate;
    private final LocalDate purchaseDate;
    private final int medicineType;
    private int remainingPuffs;
    private final double price;

    private final String brandName;

    Medicine (int medicineId, LocalDate expireDate, LocalDate purchaseDate, int medicineType, int remainingPuffs, double price, @Nullable String brandName) {

        this.medicineId = medicineId;
        this.expireDate = expireDate;
        this.purchaseDate = purchaseDate;
        this.medicineType = medicineType;
        this.remainingPuffs = remainingPuffs;
        this.price = price;
        this.brandName = brandName;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public double getPrice() {
        return price;
    }

    public int getMedicineType() {
        return medicineType;
    }
    public int getRemainingPuffs(){
        return remainingPuffs;
    }
    public String getBrandName() {
        return brandName;
    }

    public int getMedicineId()
    {
        return medicineId;
    }

    public void setRemainingPuffs(int remainingPuffs) {
        this.remainingPuffs = remainingPuffs;
    }
}
