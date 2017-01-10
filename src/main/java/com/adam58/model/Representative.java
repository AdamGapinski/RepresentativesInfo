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

    private double totalExpenses;
    private double minorRenovationExpenses;

    private List<Integer> termsOfOffice = new ArrayList<>();
    private List<BusinessTrip> trips = new ArrayList<>();

    public Representative(RepresentativeDTO representativeDTO) {
        this.name = representativeDTO.data.name;
        this.secondName = representativeDTO.data.secondName;
        this.surname = representativeDTO.data.surname;

        this.termsOfOffice = representativeDTO.data.termOfOffice;
    }
}
