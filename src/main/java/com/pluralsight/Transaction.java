package com.pluralsight;

public class Transaction {
    private String date;
    private String time;
    private String description;
    private String vendor;
    private String price;

    public Transaction(String date, String time, String description, String vendor, String price) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%-10s  %-9s  %-30s  %-20s  %10s",
        this.date, this.time, this.description, this.vendor, this.price);
    }
}
