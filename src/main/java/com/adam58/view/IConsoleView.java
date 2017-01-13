package com.adam58.view;

/**
 * @author Adam Gapiński
 */
public interface IConsoleView {
    void printSumOfExpenses(String name, String surname);
    void printOfficeRenovationExpenses(String name, String surname);
    void printSumOfExpenses(String name, String secondName, String surname);
    void printOfficeRenovationExpenses(String name, String secondName, String surname);
    void printAverageExpenses(int termOfOffice);
    void printMostBusinessTripsAbroad(int termOfOffice);
    void printLongestAbroadResidence(int termOfOffice);
    void printMostExpensiveTripAbroad(int termOfOffice);
    void printVisitedItaly(int termOfOffice);
    void printHelp();
}
