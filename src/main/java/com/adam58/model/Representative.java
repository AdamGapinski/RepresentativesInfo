package com.adam58.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Gapiński
 */
public class Representative {
    private String name;
    private String surname;

    private double totalExpenses;
    private double minorRenovationExpenses;

    private List<Integer> termsOfOffice = new ArrayList<>();
    private List<BusinessTrip> trips = new ArrayList<>();

    public Representative(RepresentativeDTO representativeDTO) {

    }
}
