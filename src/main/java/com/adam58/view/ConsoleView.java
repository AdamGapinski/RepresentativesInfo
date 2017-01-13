package com.adam58.view;

import com.adam58.model.IRepresentativesDataModel;
import com.adam58.model.Representative;
import com.adam58.model.RepresentativeNotFoundException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

/**
 * @author Adam Gapiński
 */
public class ConsoleView implements IConsoleView {
    private IRepresentativesDataModel representativesData;

    public ConsoleView(IRepresentativesDataModel representativesData) {
        this.representativesData = representativesData;
    }

    @Override
    public void printSumOfExpenses(String name, String surname) {
        Representative rep = this.representativesData.getRepresentative(name, surname);
        System.out.printf("Suma wydatków posła %s wynosi: %.2f PLN\n",
                rep.getNameDopelniacz(),
                rep.getTotalExpenses());
    }

    @Override
    public void printOfficeRenovationExpenses(String name, String surname) {
        Representative rep = representativesData.getRepresentative(name, surname);

        System.out.printf("Suma wydatków posła %s na drobne naprawy i " +
                        "remonty biura poselskiego wynosi: %.2f PLN\n",
                rep.getNameDopelniacz(),
                rep.getMinorRenovationExp());
    }

    @Override
    public void printSumOfExpenses(String name, String secondName, String surname) {
        Representative rep = this.representativesData.getRepresentative(name, secondName, surname);
        System.out.printf("Poseł %s w sumie wydał: %.2f PLN\n",
                rep, rep.getTotalExpenses());
    }

    @Override
    public void printOfficeRenovationExpenses(String name, String secondName, String surname) {
        Representative rep = representativesData.getRepresentative(name, secondName, surname);

        System.out.printf("Poseł %s w sumie wydał na drobne naprawy i " +
                        "remonty biura poselskiego: %.2f PLN\n",
                rep, rep.getMinorRenovationExp());
    }

    @Override
    public void printAverageExpenses(int termOfOffice) {
        List<Representative> representatives = representativesData.getRepsByTermOfOffice(termOfOffice);
        double sum = representatives.stream().mapToDouble(Representative::getTotalExpenses).sum();
        double average = 0;

        if (representatives.size() == 0) {
            throw new RepresentativeNotFoundException("No representatives were found for given term: " + termOfOffice);
        }

        average = sum / representatives.size();
        System.out.printf("Średnia wydatków posłów w %d. kadencji wynosi: %.2f PLN\n", termOfOffice,
                average);
    }

    @Override
    public void printMostBusinessTripsAbroad(int termOfOffice) {
        Representative rep = representativesData.getRepMostTrips(termOfOffice);

        System.out.printf("Posłem o największej liczbie (%d) podróży zagranicznych " +
                "w %d. kadencji jest: %s\n",
                rep.getBusinessTripsCount(),
                termOfOffice,
                rep.toString());
    }

    @Override
    public void printLongestAbroadResidence(int termOfOffice) {
        Representative rep = representativesData.getRepLongestTripsResidency(termOfOffice);

        System.out.printf("Poseł który najdłużej przebywał za granicą (%d dni) to: %s\n",
                rep.calculateTotalBusinessTripsResidency(),
                rep.toString());
    }

    @Override
    public void printMostExpensiveTripAbroad(int termOfOffice) {
        Representative rep = representativesData.getRepMostExpensiveTrip(termOfOffice);

        System.out.printf("Poseł który odbył najdroższą podróż zagraniczną (%.2f PLN) to: %s\n",
                rep.calculateMostExpensiveTripPrice(),
                rep.toString());
    }

    @Override
    public void printVisitedItaly(int termOfOffice) {
        System.out.printf("Posłowie, którzy w %d. kadencji odwiedzili Włochy: \n\n", termOfOffice);

        representativesData.getRepsByTripDestination("Włochy", termOfOffice)
                .forEach(System.out::println);

        System.out.println();
    }

    @Override
    public void printHelp() {
        try (Scanner scanner = new Scanner(new FileReader("help"))) {
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("something went wrong and help is not accessible.");
        }
    }
}
