package com.adam58.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * @author Adam Gapiński
 */
public class RepresentativesDataModel implements IRepresentativesDataModel {
    List<Representative> representatives = new CopyOnWriteArrayList<>();
    IFetchRepresentativesData dataProvider = new FetchRepresentativesFromWebApi();


    @Override
    public Representative getRepresentative(String name, String surname) {
        try {
            return dataProvider.fetchRepresentativeByName(name, surname);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Representative getRepresentative(String name, String secondName, String surname) {
        try {
            return dataProvider.fetchRepresentativeByName(name, secondName, surname);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private Representative getRepMostOf(int termOfOffice, BinaryOperator<Representative> operator) {
        return dataProvider.fetchRepresentativesByTermOfOffice(termOfOffice)
                .stream()
                .reduce(operator)
                .get();
    }

    @Override
    public Representative getRepMostTrips(int termOfOffice) {
        return getRepMostOf(termOfOffice, (representative, representative2) ->
                (representative.getBusinessTripsCount() > representative2.getBusinessTripsCount()) ?
                representative : representative2);
    }

    @Override
    public Representative getRepLongestTripsResidency(int termOfOffice) {
        return getRepMostOf(termOfOffice, (representative, representative2) ->
                (representative.calculateTotalBusinessTripsResidency() >
                        representative2.calculateTotalBusinessTripsResidency()) ?
                        representative : representative2);
    }

    @Override
    public Representative getRepMostExpensiveTrip(int termOfOffice) {
        return getRepMostOf(termOfOffice, (representative, representative2) ->
                (representative.calculateMostExpensiveTripPrice() > representative2.calculateMostExpensiveTripPrice()) ?
                        representative : representative2);
    }

    @Override
    public List<Representative> getRepsByTermOfOffice(int termOfOffice) {
        return dataProvider.fetchRepresentativesByTermOfOffice(termOfOffice);
    }

    @Override
    public List<Representative> getRepsByTripDestination(String destination, int termOfOffice) {

        return dataProvider.fetchRepresentativesByTermOfOffice(termOfOffice)
                .stream()
                .filter(representative -> representative.wereOnBusinessTripIn("Włochy"))
                .collect(Collectors.toList());
    }
}
