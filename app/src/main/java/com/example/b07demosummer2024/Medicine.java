package com.example.b07demosummer2024;

import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Medicine {
    private String medicineId;
    private long expireDate;
    private long purchaseDate;
    private String medicineType;
    private int remainingPuffs;
    private int maxPuffs;
    private double price;

    private String brandName;

    Medicine (){}

    Medicine (String medicineId, long expireDate, long purchaseDate, String medicineType,
              int remainingPuffs, int maxPuffs, double price, @Nullable String brandName)
    {
        this.medicineId = medicineId;
        this.expireDate = expireDate;
        this.purchaseDate = purchaseDate;
        this.medicineType = medicineType;
        this.remainingPuffs = remainingPuffs;
        this.price = price;
        this.brandName = brandName;
        this.maxPuffs = maxPuffs;
    }

    public int getMaxPuffs()
    {
        return maxPuffs;
    }

    public void setMaxPuffs(int maxPuffs)
    {
        this.maxPuffs = maxPuffs;
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
    public String getMedicineId()
    {
        return medicineId;
    }

    public void setMedicineId(String medicineId)
    {
        this.medicineId = medicineId;
    }

    public String getFormattedDateTime(long timeStamp, String formatString)
    {
        if (formatString == null || formatString.isEmpty()) {
            formatString = "MM/dd/yyyy";
        }
        Date date = new Date(timeStamp);
        SimpleDateFormat formatter = new SimpleDateFormat(formatString, Locale.getDefault());
        return formatter.format(date);
    }

    public String getExpireState()
    {
        if (System.currentTimeMillis() >= expireDate) return "Expired";
        else
            if (expireDate - System.currentTimeMillis() < 7 * 24 * 60 * 60 * 1000) return "Expire Soon";
            else return "Normal";
    }

    public String getInventoryState()
    {
        if (remainingPuffs <= 0) return "Out of Stack";
        else if (remainingPuffs <= maxPuffs / 5) return "Low Inventory";
        else return "Normal";
    }
}
