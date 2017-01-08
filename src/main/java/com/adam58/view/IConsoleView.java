package com.adam58.view;

/**
 * @author Adam Gapiński
 */
public interface IConsoleView {
    void printSumOfExpenses(String name, int termOfOffice);
    void printOfficeRenovationExpenses(String name, int termOfOffice);
    void printAverageExpenses(int termOfOffice);
    void printMostBusinessTripsAbroad(int termOfOffice);
    void printLongestAbroadResidence(int termOfOffice);
    void printMostExpensiveTripAbroad(int termOfOffice);
    void printVisitedItaly(int termOfOffice);
}
