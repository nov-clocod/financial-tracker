package com.pluralsight;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            System.out.println("Choose an option:");
            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */
    public static void loadTransactions(String fileName) {

        try {
            BufferedReader myReader = new BufferedReader(new FileReader(fileName));
            File file = new File(fileName);

            if (!file.exists()) {
                BufferedWriter myWriter = new BufferedWriter(new FileWriter(fileName));
                Transaction firstTransaction = new Transaction(
                        LocalDate.parse("2025-10-10", DATE_FMT),
                        LocalTime.parse("11:35:56", TIME_FMT),
                        "Starting Balance",
                        "Myself",
                        5.00);

                myWriter.write(String.format("%s|%s|%s|%s|%s\n",
                        firstTransaction.getDate(),
                        firstTransaction.getTime(),
                        firstTransaction.getDescription(),
                        firstTransaction.getVendor(),
                        firstTransaction.getPrice()));

            myWriter.close();

            } else {
                String line;

                while ((line = myReader.readLine()) != null) {
                    String[] section = line.split("\\|");
                    LocalDate transactionDate = LocalDate.parse(section[0]);
                    LocalTime transactionTime = LocalTime.parse(section[1]);
                    String transactionDescription = section[2];
                    String transactionVendor = section[3];
                    double transactionPrice = Double.parseDouble(section[4]);

                    transactions.add(new Transaction(
                            transactionDate,
                            transactionTime,
                            transactionDescription,
                            transactionVendor,
                            transactionPrice));
                }
            }

            myReader.close();

        } catch (Exception exception) {
            System.out.println("Oops, something went wrong");
            System.out.println(exception.getMessage());
        }

    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */
    private static void addDeposit(Scanner scanner) {
        try {
            BufferedWriter myWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            System.out.println("Enter the date with time in this format (yyyy-MM-dd HH:mm:s): ");
            String userInputDateTime = scanner.nextLine().trim();

            System.out.println("Enter the description of the deposit: ");
            String userInputDescriptionDeposit = scanner.nextLine().trim();

            System.out.println("Enter the payer/institution's name: ");
            String userInputVendor = scanner.nextLine().trim();

            System.out.println("Enter the deposit amount: ");
            double userDepositAmount = scanner.nextDouble();
            scanner.nextLine();

            String formattedAmount = String.format("%.2f", userDepositAmount);
            int spacePosition = userInputDateTime.indexOf(" ");
            myWriter.write(userInputDateTime.substring(0, spacePosition) + "|" +
                    userInputDateTime.substring(spacePosition + 1) + "|" +
                    userInputDescriptionDeposit + "|" +
                    userInputVendor + "|" +
                    formattedAmount + "\n");

            System.out.println("Deposit recorded\n");
            myWriter.close();

        } catch (Exception exception) {
            System.out.println("Error writing to the file");
            System.out.println(exception.getMessage());
        }

    }

    private static void addPayment(Scanner scanner) {

        try {
            BufferedWriter myWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            System.out.println("Enter the date with time in this format (yyyy-MM-dd HH:mm:s): ");
            String userInputDateTime = scanner.nextLine().trim();

            System.out.println("Enter the description of the payment: ");
            String userInputDescriptionDeposit = scanner.nextLine().trim();

            System.out.println("Enter the payee/institution's name: ");
            String userInputVendor = scanner.nextLine().trim();

            System.out.println("Enter the payment amount: ");
            double userDepositAmount = scanner.nextDouble();
            scanner.nextLine();

            String formattedAmount = String.format("-%.2f", userDepositAmount);
            int spacePosition = userInputDateTime.indexOf(" ");
            myWriter.write(userInputDateTime.substring(0, spacePosition) + "|" +
                    userInputDateTime.substring(spacePosition + 1) + "|" +
                    userInputDescriptionDeposit + "|" +
                    userInputVendor + "|" +
                    formattedAmount + "\n");

            System.out.println("Payment recorded\n");
            myWriter.close();

        } catch (Exception exception) {
            System.out.println("Error writing to the file");
            System.out.println(exception.getMessage());
        }

    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.println("Choose an option:");
            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {

        try {
            BufferedReader myReader = new BufferedReader(new FileReader(FILE_NAME));
            System.out.println("Date        Time       Description                     Vendor                    Amount");
            System.out.println("---------------------------------------------------------------------------------------");

            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }

            myReader.close();

        } catch (Exception exception) {
            System.out.println("Error writing to the file");
            System.out.println(exception.getMessage());
        }
    }

    private static void displayDeposits() {

        try {
            BufferedReader myReader = new BufferedReader(new FileReader(FILE_NAME));
            System.out.println("Date        Time       Description                     Vendor                    Amount");
            System.out.println("---------------------------------------------------------------------------------------");

            for (Transaction transaction : transactions) {
                String priceString = String.valueOf(transaction.getPrice());
                if (!priceString.startsWith("-")) {
                    System.out.println(transaction);
                }
            }

            myReader.close();

        } catch (Exception exception) {
            System.out.println("Error writing to the file");
            System.out.println(exception.getMessage());
        }

    }

    private static void displayPayments() {

        try {
            BufferedReader myReader = new BufferedReader(new FileReader(FILE_NAME));
            System.out.println("Date        Time       Description                     Vendor                    Amount");
            System.out.println("---------------------------------------------------------------------------------------");

            for (Transaction transaction : transactions) {
                String priceString = String.valueOf(transaction.getPrice());
                if (priceString.startsWith("-")) {
                    System.out.println(transaction);
                }
            }

            myReader.close();

        } catch (Exception exception) {
            System.out.println("Error writing to the file");
            System.out.println(exception.getMessage());
        }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");
            System.out.println("Choose an option:");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> monthToDateReport();
                case "2" -> previousMonthReport();
                case "3" -> {/* TODO – year-to-date report   */ }
                case "4" -> {/* TODO – previous year report  */ }
                case "5" -> {/* TODO – prompt for vendor then report */ }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void previousMonthReport() {
        LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = LocalDate.now().withDayOfMonth(1).minusDays(1);

        filterTransactionsByDate(firstDayOfLastMonth, lastDayOfLastMonth);
    }

    private static void monthToDateReport() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();

        filterTransactionsByDate(firstDayOfMonth, today);
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // TODO – iterate transactions, print those within the range
        for (Transaction transaction : transactions) {
            if ((transaction.getDate().isAfter(start) || transaction.getDate().isEqual(start)) &&
                    (transaction.getDate().isBefore(end)  || transaction.getDate().isEqual(end))) {
                System.out.println(transaction);
            }
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
    }

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {

        if (s == null) {
            return null;
        } else {
            return LocalDate.parse(s, DATE_FMT);
        }
    }

    private static Double parseDouble(String s) {
        /* TODO – return Double   or null */
        return null;
    }
}
