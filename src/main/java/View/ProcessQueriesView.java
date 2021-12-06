package View;

import Controller.ProcessQuery;
import View.printer.Printer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ProcessQueriesView {
    private Printer printer;
    private Scanner scanner;
    private static FileWriter fileWriter;

    static {
        try {
            fileWriter = new FileWriter("./src/main/java/Logs/QueryLogs.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ProcessQuery processQuery = new ProcessQuery(fileWriter);


    ProcessQueriesView(Scanner scanner, Printer print) throws IOException {
        this.printer = print;
        this.scanner = scanner;
    }

    public void displayMenu() throws Exception {
        while (true) {
            printer.printTitle("Process SQL Queries");
            printer.printString("1. Execute Query");
            printer.printString("2. Return");
            final String input = scanner.nextLine();
            switch (input) {
                case "1":
                    printer.printString("Enter your SQL Query:");
                    String query = scanner.nextLine();
                    printer.printString(processQuery.processorQuery(query));
                    break;
                case "2":
                    processQuery.closeFileWriter();
                    return;
            }
        }

    }



}
