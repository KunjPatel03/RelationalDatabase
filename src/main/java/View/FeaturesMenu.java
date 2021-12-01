package View;

import View.printer.Printer;
import java.util.Scanner;

public class FeaturesMenu {
    private Printer printer;
    private Scanner scanner;

    public FeaturesMenu(Scanner scanner, Printer print){
        this.printer = print;
        this.scanner = scanner;
    }

    public void displayMenu() throws Exception{
        while (true) {
            printer.printTitle("Main Menu");
            printer.printString("1. Process SQL Queries");
            printer.printString("2. Generate Dump");
            printer.printString("3. Generate ERD");
            printer.printString("4. Logout");
            printer.printString("Select an option:");
            final String input = scanner.nextLine();

            switch (input) {
                case "1":
                    ProcessQueriesView processQueriesView = new ProcessQueriesView(scanner,printer);
                    processQueriesView.displayMenu();
                    break;
                case "2":
                    ExportDumpView exportDumpView = new ExportDumpView(scanner, printer);
                    exportDumpView.displaySchemas();
                    break;
                case "3":
                    System.out.println("selected 3");
                    break;
                case "4":
                    System.out.println("selected 4");
                    return;
                default:
                    break;
            }
        }
    }
}
