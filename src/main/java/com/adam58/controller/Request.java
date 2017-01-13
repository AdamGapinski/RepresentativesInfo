package com.adam58.controller;

/**
 * @author Adam Gapiński
 */
public enum Request {
    SINGLE_REP_INFO, REPRESENTATIVES_INFO, HELP;

    public enum SingleRepInfo {
        TOTAL_EXPENSES, MINOR_RENOVATION_EXPENSES, ALL
    }

    public enum RepresentativeInfo {
        AVERAGE_EXP, MOST_EXP_TRIP, LONGEST_ON_TRIP, MOST_TRIPS, VISITED_ITALY, ALL
    }
}
