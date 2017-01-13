package com.adam58.model;

/**
 * @author Adam Gapiński
 */
public class BusinessTrip {
    private String country;
    private int days;
    private double totalExpense;

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }


    public String getCountry() {
        return country;
    }

    public int getDays() {
        return days;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }
}
