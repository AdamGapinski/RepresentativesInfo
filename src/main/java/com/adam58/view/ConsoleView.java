package com.adam58.view;

import com.adam58.model.IRepresentativesDataModel;

/**
 * @author Adam Gapiński
 */
public class ConsoleView implements IConsoleView {
    private IRepresentativesDataModel representativesData;

    public ConsoleView(IRepresentativesDataModel representativesData) {
        this.representativesData = representativesData;
    }

    @Override
    public void printSumOfExpenses(String name, int termOfOffice) {

    }

    @Override
    public void printOfficeRenovationExpenses(String name, int termOfOffice) {

    }

    @Override
    public void printAverageExpenses(int termOfOffice) {

    }

    @Override
    public void printMostBusinessTripsAbroad(int termOfOffice) {

    }

    @Override
    public void printLongestAbroadResidence(int termOfOffice) {

    }

    @Override
    public void printMostExpensiveTripAbroad(int termOfOffice) {

    }

    @Override
    public void printVisitedItaly(int termOfOffice) {

    }
}
