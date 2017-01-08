package com.adam58.model;

import java.util.List;

/**
 * @author Adam Gapiński
 */
public interface IRepresentativesDataModel {
    Representative getRep(String name);
    Representative getRepMostTrips(int termOfOffice);
    Representative getRepLongestTripsResidency(int termOfOffice);
    Representative getRepMostExpensiveTrip(int termOfOffice);
    List<Representative> getRepsByTermOfOffice(int termOfOffice);
    List<Representative> getRepsByTripDestination(String destination,
                                                  int termOfOffice);
}
