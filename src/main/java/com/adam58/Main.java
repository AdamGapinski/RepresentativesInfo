package com.adam58;

import com.adam58.controller.Controller;

/**
 * @author Adam Gapiński
 */
public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.handleUserRequest(args);
    }
}
