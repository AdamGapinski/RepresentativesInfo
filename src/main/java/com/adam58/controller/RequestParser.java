package com.adam58.controller;

import java.util.Arrays;

import static com.adam58.controller.Request.*;


/**
 * @author Adam Gapiński
 */
public class RequestParser implements IRequestParser {
    @Override
    public Request parseRequest(String[] args) {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException("Request not specified, run with -help for more info");
        }

        String requestString = args[0];
        requestString = requestString.toUpperCase();

        Request request;
        switch (requestString.toLowerCase()) {
            case "-help":
                request = HELP;
                break;
            case "-n":
                request = SINGLE_REP_INFO;
                break;
            case "-k":
                request = REPRESENTATIVES_INFO;
                break;
            default:
                throw new RequestNotSupported("Request not supported, run with -help for more info");
        }

        return request;
    }

    private int parseTermNumber(String stringNumber) {
        int number;

        number = parserRomanNumber(stringNumber);

        if (number == -1) {
            number = parseNumber(stringNumber, "term");
        }

        return number;
    }

    private int parserRomanNumber(String stringNumber) {
        stringNumber = stringNumber.toUpperCase();

        String[] romanNumber = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

        int index = Arrays.asList(romanNumber).indexOf(stringNumber);

        return index == -1 ? -1 : index + 1;
    }

    private int parseNumber(String stringNumber, String description) {
        int number;

        try {
            number = Integer.valueOf(stringNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("%s is not valid %s number", stringNumber, description));
        }

        return number;
    }

    @Override
    public RepresentativeInfo parseRepsInfoRequest(String[] args) {
        RepresentativeInfo request;
        String stringRequest = "";

        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                stringRequest = args[i];
                break;
            }
        }

        switch (stringRequest.toLowerCase()) {
            case "-srednia":
                request = RepresentativeInfo.AVERAGE_EXP;
                break;
            case "-najwiecej":
                request = RepresentativeInfo.MOST_TRIPS;
                break;
            case "-najdluzej":
                request = RepresentativeInfo.LONGEST_ON_TRIP;
                break;
            case "-najdrozej":
                request = RepresentativeInfo.MOST_EXP_TRIP;
                break;
            case "-wlochy":
                request = RepresentativeInfo.VISITED_ITALY;
                break;
            default:
                request = RepresentativeInfo.ALL;
                break;
        }
        return request;
    }

    @Override
    public SingleRepInfo parseSingleRepRequest(String[] args) {

        SingleRepInfo request;
        String stringRequest = "";

        for (int i = 1; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                stringRequest = args[i];
                break;
            }
        }

        switch (stringRequest.toLowerCase()) {
            case "-suma":
                request = SingleRepInfo.TOTAL_EXPENSES;
                break;
            case "-drobne":
                request = SingleRepInfo.MINOR_RENOVATION_EXPENSES;
                break;
            default:
                request = SingleRepInfo.ALL;
                break;
        }
        return request;
    }

    @Override
    public String parseName(String[] args) {
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                resultBuilder.append(args[i]);
                resultBuilder.append(" ");
            }
        }

        String result = resultBuilder.toString().trim();

        if (result.split(" ").length < 2) {
            throw new InvalidParameter(String.format("Given name: %s is too short", result));
        } else if (result.split(" ").length > 3) {
            throw new InvalidParameter(String.format("Given name: %s is too long", result));
        }

        return result;
    }

    @Override
    public int parseNumber(String[] args) {
        int term = 0;

        boolean found = false;
        for (int i = 1; i < args.length && !found; i++) {
            if (!args[i].startsWith("-")) {
                term = parseTermNumber(args[i]);
                found = true;
            }
        }

        if (!found) {
            throw new NotEnoughArgumentsException("Term number not specified, run with -help for more info");
        }

        return term;
    }
}
class RequestNotSupported extends RuntimeException {
    RequestNotSupported(String message) {
        super(message);
    }
}

class InvalidParameter extends RuntimeException {
    InvalidParameter(String message) {
        super(message);
    }
}

class NotEnoughArgumentsException extends RuntimeException {
    NotEnoughArgumentsException(String message) {
        super(message);
    }
}
