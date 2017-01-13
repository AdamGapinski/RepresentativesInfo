package com.adam58.controller;

import com.adam58.model.RepresentativeNotFoundException;
import com.adam58.model.RepresentativesDataModel;
import com.adam58.view.ConsoleView;
import com.adam58.view.IConsoleView;

/**
 * @author Adam Gapiński
 */
public class Controller {
    private IConsoleView consoleView = new ConsoleView(new RepresentativesDataModel());
    private IRequestParser parser = new RequestParser();

    public void handleUserRequest(String[] args) {
        try {
            Request request = parser.parseRequest(args);

            switch (request) {
                case SINGLE_REP_INFO:
                    showSingleRepInfo(args);
                    break;
                case REPRESENTATIVES_INFO:
                    showRepsInfo(args);
                    break;
                case HELP:
                    consoleView.printHelp();
                    break;
            }

        } catch (NotEnoughArgumentsException |
                InvalidParameter |
                RequestNotSupported |
                RepresentativeNotFoundException e){
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }
    }

    private void showRepsInfo(String[] args) {
        int term = parser.parseNumber(args);

        switch (parser.parseRepsInfoRequest(args)) {
            case AVERAGE_EXP:
                consoleView.printAverageExpenses(term);
                break;
            case MOST_EXP_TRIP:
                consoleView.printMostExpensiveTripAbroad(term);
                break;
            case LONGEST_ON_TRIP:
                consoleView.printLongestAbroadResidence(term);
                break;
            case MOST_TRIPS:
                consoleView.printMostBusinessTripsAbroad(term);
                break;
            case VISITED_ITALY:
                consoleView.printVisitedItaly(term);
                break;
            case ALL:
                consoleView.printAverageExpenses(term);
                consoleView.printMostExpensiveTripAbroad(term);
                consoleView.printMostBusinessTripsAbroad(term);
                consoleView.printLongestAbroadResidence(term);
                consoleView.printVisitedItaly(term);
                break;
        }
    }

    private void showSingleRepInfo(String[] args) {
        String name = parser.parseName(args);

        switch (parser.parseSingleRepRequest(args)) {
            case TOTAL_EXPENSES:
                showTotalExpenses(name);
                break;
            case MINOR_RENOVATION_EXPENSES:
                showMinorRenovationExpenses(name);
                break;
            case ALL:
                showTotalExpenses(name);
                showMinorRenovationExpenses(name);
                break;
        }
    }

    private void showTotalExpenses(String name) {
        String namePart[] = name.split(" ");

        if (namePart.length == 2) {
            consoleView.printSumOfExpenses(namePart[0], namePart[1]);
        } else if (namePart.length == 3){
            consoleView.printSumOfExpenses(namePart[0], namePart[1], namePart[2]);
        }
    }

    private void showMinorRenovationExpenses(String name) {
        String namePart[] = name.split(" ");

        if (namePart.length == 2) {
            consoleView.printOfficeRenovationExpenses(namePart[0], namePart[1]);
        } else if (namePart.length == 3){
            consoleView.printOfficeRenovationExpenses(namePart[0], namePart[1], namePart[2]);
        }
    }
}
