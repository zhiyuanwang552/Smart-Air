package com.example.b07demosummer2024;

import androidx.annotation.Nullable;
import java.time.LocalDateTime;


public class Medicine {
    private final int MedicineId;
    private final LocalDateTime expireDate;
    private final LocalDateTime purchaseDate;
    private final int medicineType;
    private int remainingPuffs;
    private final double price;

    private final String brandName;

    Medicine (int medicineId, LocalDateTime expireDate, LocalDateTime purchaseDate, int medicineType, int remainingPuffs, double price, @Nullable String brandName) {

        this.MedicineId = medicineId;
        this.expireDate = expireDate;
        this.purchaseDate = purchaseDate;
        this.medicineType = medicineType;
        this.remainingPuffs = remainingPuffs;
        this.price = price;
        this.brandName = brandName;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public LocalDateTime getPurchaseDate() {
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

    public void setRemainingPuffs(int remainingPuffs) {
        this.remainingPuffs = remainingPuffs;
    }
}
