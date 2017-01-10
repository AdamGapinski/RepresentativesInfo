package com.adam58.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Gapiński
 */
public class Representative {
    private String name;
    private String names;
    private String surname;
    private String secondName;

    private double totalExpenses;
    private double minorRenovationExp;

    private List<Integer> termsOfOffice = new ArrayList<>();
    private List<BusinessTrip> trips = new ArrayList<>();

    public Representative(RepresentativeDTO representativeDTO) {
        this.name = representativeDTO.data.name;
        this.names = representativeDTO.data.names;
        this.secondName = representativeDTO.data.secondName;
        this.surname = representativeDTO.data.surname;

        this.termsOfOffice = representativeDTO.data.termOfOffice;
        representativeDTO.layers.expenses.punkty.forEach(tuple -> {
            this.totalExpenses += tuple.number;
        });


        /*representativeDTO.layers.expenses.punkty
                .stream()
                .filter(tuple -> tuple.title.equals("Koszty drobnych napraw i remontów lokalu biura poselskiego"))
                .forEach(tuple -> {
                    for (List<Integer> value :
                            representativeDTO.layers.expenses.values) {
                        this.minorRenovationExp += value.get(tuple.number - 1);
                    }
                });*/
    }
}
