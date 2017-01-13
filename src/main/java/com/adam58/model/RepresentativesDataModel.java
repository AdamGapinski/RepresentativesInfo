package com.adam58.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * @author Adam Gapiński
 */
public class RepresentativesDataModel implements IRepresentativesDataModel {
    private List<Representative> representatives = new ArrayList<>();
    private List<Integer> fetchedTerms = new ArrayList<>();
    private IFetchRepresentativesData dataProvider = new FetchRepresentativesFromWebApi();


    @Override
    public Representative getRepresentative(String name, String surname) {
        Representative result;

        List<Representative> resultList = representatives.stream().filter(representative ->
                representative.getName().toLowerCase().equals(name.toLowerCase()) &&
                        representative.getSurname().toLowerCase().equals(surname.toLowerCase()) &&
                        representative.getSecondName() == null)
                .collect(Collectors.toList());

        if (resultList.size() == 0) {
            result = dataProvider.fetchRepresentativeByName(name, surname);
            representatives.add(result);
        } else {
            result = resultList.get(0);
        }

        return result;
    }

    @Override
    public Representative getRepresentative(String name, String secondName, String surname) {
        Representative result;

        List<Representative> resultList = representatives.stream().filter(representative ->
                representative.getName().toLowerCase().equals(name.toLowerCase()) &&
                        representative.getSurname().toLowerCase().equals(surname.toLowerCase()) &&
                        representative.getSecondName().toLowerCase().equals(secondName.toLowerCase()))
                .collect(Collectors.toList());

        if (resultList.size() == 0) {
            result = dataProvider.fetchRepresentativeByName(name, secondName, surname);
            representatives.add(result);
        } else {
            result = resultList.get(0);
        }

        return result;
    }


    private Representative getRepMostOf(int termOfOffice, BinaryOperator<Representative> operator) {
        return this.getRepsByTermOfOffice(termOfOffice)
                .stream()
                .reduce(operator)
                .orElseThrow(() -> new RepresentativeNotFoundException(String.format("Representatives not found for " +
                        "given term: %d", termOfOffice)));
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
        List<Representative> result;

        if (fetchedTerms.contains(termOfOffice)) {
            result = representatives.stream()
                    .filter(representative -> representative.hasRepresentedInTerm(termOfOffice))
                    .collect(Collectors.toList());
        } else {
            result = dataProvider.fetchRepresentativesByTermOfOffice(termOfOffice);
            fetchedTerms.add(termOfOffice);
            representatives.addAll(result);
        }

        return result;
    }

    @Override
    public List<Representative> getRepsByTripDestination(String destination, int termOfOffice) {
        return this.getRepsByTermOfOffice(termOfOffice)
                .stream()
                .filter(representative -> representative.wereOnBusinessTripIn("Włochy"))
                .collect(Collectors.toList());
    }
}