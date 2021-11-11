package View;

import View.printer.Printer;

import java.util.Scanner;

public class ProcessQueriesView {
    private Printer printer;
    private Scanner scanner;
    ProcessQueriesView(Scanner scanner, Printer print){
        this.printer = print;
        this.scanner = scanner;
    }

    public void displayMenu() {
        while (true) {
            printer.printTitle("Process SQL Queries");
            printer.printString("1. Execute Query");
            printer.printString("2. Return");
            final String input = scanner.nextLine();
            switch (input) {
                case "1":
                    printer.printString("Enter your SQL Query:");
                    String query = scanner.nextLine();
                    break;
                case "2": return;
            }
        }
    }
}
