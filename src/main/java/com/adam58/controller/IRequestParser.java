package com.adam58.controller;

/**
 * @author Adam Gapiński
 */
public interface IRequestParser {
    Request parseRequest(String[] args);
    Request.RepresentativeInfo parseRepsInfoRequest(String[] args);
    Request.SingleRepInfo parseSingleRepRequest(String[] args);
    String parseName(String[] args);
    int parseNumber(String[] args);
}
