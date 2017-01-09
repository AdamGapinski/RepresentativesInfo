package com.adam58.controller;

/**
 * @author Adam Gapiński
 */
public class Request {
    private int termOfOffice;

    public Request(int termOfOffice) {
        this.termOfOffice = termOfOffice;
    }

    public int getTermOfOffice() {
        return termOfOffice;
    }
}
