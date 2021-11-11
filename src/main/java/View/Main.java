package View;

import View.printer.Printer;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Printer printer = new Printer();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printer.printTitle("Welcome to RDBMS Project");
            printer.printString("1. User registration.");
            printer.printString("2. User login.");
            printer.printString("3. Exit.");
            printer.printString("Select an option:");
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    System.out.println("selected 1");
                    break;
                case "2":
                    FeaturesMenu featureMenu = new FeaturesMenu(scanner, printer);
                    featureMenu.displayMenu();
                    break;
                case "3":
                    System.out.println("selected 3");
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }
}
