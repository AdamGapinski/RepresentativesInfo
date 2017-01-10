package com.adam58.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Adam Gapiński
 */
public class RepresentativesDataModel implements IRepresentativesDataModel {
    List<Representative> representatives = new CopyOnWriteArrayList<>();

    @Override
    public Representative getRep(String name) {
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
