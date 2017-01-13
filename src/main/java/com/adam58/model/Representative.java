package com.adam58.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Gapiński
 */
public class Representative {
    private String name;
    private String surname;
    private String secondName;
    private String nameDopelniacz;

    private double totalExpenses;
    private double minorRenovationExp;

    private List<BusinessTrip> trips = new ArrayList<>();

    public Representative(){}

    public Representative(RepresentativeDTO representativeDTO) {
        this.name = representativeDTO.data.name;
        this.secondName = representativeDTO.data.secondName;
        this.surname = representativeDTO.data.surname;

        representativeDTO.layers.expenses.punkty.forEach(tuple -> this.totalExpenses += tuple.number);
    }

    @Override
    public String toString() {
        if (secondName == null) {
            return String.format("%s %s", this.getName(), this.getSurname());
        } else {
            return String.format("%s %s %s", this.getName(), this.getSecondName(), this.getSurname());
        }
    }

    public boolean wereOnBusinessTripIn(String destination) {
        return trips.stream()
                .anyMatch(businessTrip -> businessTrip
                        .getCountry()
                        .toLowerCase()
                        .equals(destination.toLowerCase()));
    }

    public int calculateTotalBusinessTripsResidency() {
        return trips.stream().mapToInt(BusinessTrip::getDays).sum();
    }

    public double calculateMostExpensiveTripPrice() {
        return trips.stream()
                .mapToDouble(BusinessTrip::getTotalExpense)
                .reduce(0, (left, right) -> left > right ? left : right);
    }

    public int getBusinessTripsCount() {
        return trips.size();
    }

    public void addBusinessTrip(BusinessTrip businessTrip) {
        trips.add(businessTrip);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getNameDopelniacz() {
        return nameDopelniacz;
    }

    public void setNameDopelniacz(String nameDopelniacz) {
        this.nameDopelniacz = nameDopelniacz;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public void setMinorRenovationExp(double minorRenovationExp) {
        this.minorRenovationExp = minorRenovationExp;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public double getMinorRenovationExp() {
        return minorRenovationExp;
    }
}
