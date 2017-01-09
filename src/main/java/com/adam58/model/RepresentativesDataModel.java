package com.adam58.model;

import com.google.gson.Gson;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public class RepresentativesDataModel implements IRepresentativesDataModel {
    @Override
    public Representative getRep(String name) {
        name = name.toUpperCase();

        Gson gson = new Gson();


        return null;
    }

    @Override
    public Representative getRepMostTrips(int termOfOffice) {
        return null;
    }

    @Override
    public Representative getRepLongestTripsResidency(int termOfOffice) {
        return null;
    }

    @Override
    public Representative getRepMostExpensiveTrip(int termOfOffice) {
        return null;
    }

    @Override
    public List<Representative> getRepsByTermOfOffice(int termOfOffice) {
        return null;
    }

    @Override
    public List<Representative> getRepsByTripDestination(String destination, int termOfOffice) {
        return null;
    }
}
