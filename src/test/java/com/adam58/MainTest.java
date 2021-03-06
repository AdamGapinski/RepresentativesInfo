package com.adam58;

import com.adam58.controller.Controller;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adam Gapiński
 */
public class MainTest {
    private Controller controller = new Controller();
    private String[] args;

    @Test
    public void testSumInfoForRepresentative() {
        args = new String[]{"-n", "Adam Stanisław Szejnfeld", "-suma"};
        controller.handleUserRequest(args);

        args = new String[] {"-n", "-suma", "Adam Stanisław Szejnfeld"};
        controller.handleUserRequest(args);
    }

    @Test
    public void testMinorExpensesInfoForRepresentative() {
        args = new String[]{"-n", "Adam Stanisław Szejnfeld", "-drobne"};
        controller.handleUserRequest(args);
    }

    @Test
    public void testInfoForRepresentative() {
        args = new String[]{"-n", "Adam Stanisław Szejnfeld"};
        controller.handleUserRequest(args);
    }

    @Test
    public void testSingleRepresentativeInfo() {
        String tableOfArgs[][] = new String[][] {
                {"-n", "Krystyna Poślednia", "-drobne"},
                {"-n", "-suma", "Elżbieta Barbara Wite"},
                {"-n", "Krzysztof Brejza", "-suma"},
                {"-n", "-drobne","Renata Celina Butryn"}
        };

        for (String[] args : tableOfArgs) {
            controller.handleUserRequest(args);
        }
    }

    @Test
    public void testInvalidCommandLineArguments () {
        String tableOfArgs[][] = new String[][] {
                    //lack of representative name
                {"-n", "-drobne"},
                    //lack of request parameter -k or -n
                {"-suma", "Elżbieta Barbara Wite"},
                    //should start with -n
                {"-k", "Krzysztof Brejza", "-suma"},
                    //lack of term of office after -k parameter
                {"-k", "-srednia"},
                    //this representative does not exist, should be not found
                {"-n", "Renata Celina"},
                    //lack of hyphen before drobne so it will be treated as a name until end of line and not found
                {"-n", "drobne","Renata Celina Butryn"}
        };

        for (String[] args : tableOfArgs) {
            controller.handleUserRequest(args);
        }
    }


    @Test
    public void testRepresentativesInfo() {
        String tableOfArgs[][] = new String[][] {
                {"-k", "8"},
                {"-k", "8", "-srednia"},
                {"-k", "8", "-najdluzej"},
                {"-k", "8", "-najwiecej"},
                {"-k", "8", "-najdrozej"},
                {"-k", "8", "-wlochy"}
        };

        for (String[] args : tableOfArgs) {
            controller.handleUserRequest(args);
        }
    }
}