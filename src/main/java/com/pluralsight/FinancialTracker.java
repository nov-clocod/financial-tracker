package com.pluralsight;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
        transactions.sort(Collections.reverseOrder());

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("Welcome to TransactionApp");
            System.out.println("-------------------------");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            System.out.println("Choose an option:");
            String input = scanner.nextLine().trim();
            System.out.println();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> {
                    running = false;
                    System.out.println("“Thanks for using our app! We hope to see you again soon.");
                }
                default -> System.out.println("Invalid option\n");
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
                        firstTransaction.getAmount()));

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
            String stringInputDepositDateTime = scanner.nextLine().trim();
            String formattedDepositDateTime = String.valueOf(LocalDateTime.parse(stringInputDepositDateTime, DATETIME_FMT));

            System.out.println("Enter the description of the deposit: ");
            String userDepositDescription = scanner.nextLine().trim();

            System.out.println("Enter the payer/institution's name: ");
            String userDepositVendor = scanner.nextLine().trim();

            System.out.println("Enter the deposit amount (positive number): ");
            String stringInputDepositAmount = scanner.nextLine().trim();
            double userDepositAmount = parseDouble(stringInputDepositAmount);

            int spacePosition = formattedDepositDateTime.indexOf("T");
            LocalDate userDepositDate = LocalDate.parse((formattedDepositDateTime.substring(0, spacePosition)));
            LocalTime userDepositTime = LocalTime.parse((formattedDepositDateTime.substring(spacePosition + 1)));

            myWriter.write(userDepositDate + "|" +
                    userDepositTime + "|" +
                    userDepositDescription + "|" +
                    userDepositVendor + "|" +
                    userDepositAmount + "\n");

            transactions.add(new Transaction(
                    userDepositDate,
                    userDepositTime,
                    userDepositDescription,
                    userDepositVendor,
                    userDepositAmount));

            transactions.sort(Collections.reverseOrder());

            System.out.println("Deposit recorded\n");
            myWriter.close();

        } catch (Exception exception) {
            System.out.println("Error writing to the file");
            System.out.println(exception.getMessage() + "\n");
        }

    }

    private static void addPayment(Scanner scanner) {

        try {
            BufferedWriter myWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            System.out.println("Enter the date with time in this format (yyyy-MM-dd HH:mm:s): ");
            String stringInputPaymentDateTime = scanner.nextLine().trim();
            String formattedPaymentDateTime = String.valueOf(LocalDateTime.parse(stringInputPaymentDateTime, DATETIME_FMT));

            System.out.println("Enter the description of the payment: ");
            String userPaymentDescription = scanner.nextLine().trim();

            System.out.println("Enter the payee/institution's name: ");
            String userPaymentVendor = scanner.nextLine().trim();

            System.out.println("Enter the payment amount (positive number): ");
            String stringInputDepositAmount = scanner.nextLine().trim();
            double userPaymentAmount = parseDouble("-" + stringInputDepositAmount);

            int spacePosition = formattedPaymentDateTime.indexOf("T");
            LocalDate userPaymentDate = LocalDate.parse((formattedPaymentDateTime.substring(0, spacePosition)));
            LocalTime userPaymentTime = LocalTime.parse((formattedPaymentDateTime.substring(spacePosition + 1)));

            myWriter.write(userPaymentDate + "|" +
                    userPaymentTime + "|" +
                    userPaymentDescription + "|" +
                    userPaymentVendor + "|" +
                    userPaymentAmount + "\n");

            transactions.add(new Transaction(userPaymentDate,
                    userPaymentTime,
                    userPaymentDescription,
                    userPaymentVendor,
                    userPaymentAmount));

            transactions.sort(Collections.reverseOrder());

            System.out.println("Payment recorded\n");
            myWriter.close();

        } catch (Exception exception) {
            System.out.println("Error writing to the file");
            System.out.println(exception.getMessage() + "\n");
        }

    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger Menu");
            System.out.println("-----------");
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
                default -> System.out.println("Invalid option\n");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {

        try {
            BufferedReader myReader = new BufferedReader(new FileReader(FILE_NAME));

            tableHeader();
            boolean found = false;

            for (Transaction transaction : transactions) {
                System.out.println(transaction);
                found = true;
            }


            if (!found) {
                System.out.println("No transaction history found! Go make your first deposit or payment!\n");
            } else {
                System.out.println();
            }

            myReader.close();

        } catch (Exception exception) {
            System.out.println("Error reading the file");
            System.out.println(exception.getMessage() + "\n");
        }
    }

    private static void displayDeposits() {

        try {
            BufferedReader myReader = new BufferedReader(new FileReader(FILE_NAME));

            tableHeader();
            boolean found = false;

            for (Transaction transaction : transactions) {
                String priceString = String.valueOf(transaction.getAmount());
                if (!priceString.startsWith("-")) {
                    System.out.println(transaction);
                    found =true;
                }
            }

            System.out.println();

            if (!found) {
                System.out.println("No deposit history found! Go make your first deposit!\n");
            }

            myReader.close();

        } catch (Exception exception) {
            System.out.println("Error writing to the file");
            System.out.println(exception.getMessage() + "\n");
        }

    }

    private static void displayPayments() {

        try {
            BufferedReader myReader = new BufferedReader(new FileReader(FILE_NAME));

            tableHeader();
            boolean found = false;

            for (Transaction transaction : transactions) {
                String priceString = String.valueOf(transaction.getAmount());
                if (priceString.startsWith("-")) {
                    System.out.println(transaction);
                    found = true;
                }
            }

            System.out.println();

            if (!found) {
                System.out.println("No payment history found! Go make your first payment!\n");
            }

            myReader.close();

        } catch (Exception exception) {
            System.out.println("Error writing to the file");
            System.out.println(exception.getMessage() + "\n");
        }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("Reports");
            System.out.println("-------");
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
                case "3" -> yearToDateReport();
                case "4" -> previousYearReport();
                case "5" -> vendorSearch(scanner);
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option\n");
            }
        }
    }

    private static void vendorSearch(Scanner scanner) {
        System.out.println("Enter the vendor's name: ");
        String inputVendor = scanner.nextLine().trim().toLowerCase();

        filterTransactionsByVendor(inputVendor);
    }

    private static void previousYearReport() {
        LocalDate firstDayOfLastYear = LocalDate.now().minusYears(1).withDayOfYear(1);
        LocalDate lastDayOfLastYear = LocalDate.now().withDayOfYear(1).minusDays(1);

        filterTransactionsByDate(firstDayOfLastYear, lastDayOfLastYear);
    }

    private static void yearToDateReport() {
        LocalDate firstDayOfThisYear = LocalDate.now().withDayOfYear(1);
        LocalDate today = LocalDate.now();

        filterTransactionsByDate(firstDayOfThisYear, today);
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

        tableHeader();

        boolean found = false;
        for (Transaction transaction : transactions) {
            if ((transaction.getDate().isAfter(start) || transaction.getDate().isEqual(start)) &&
                    (transaction.getDate().isBefore(end)  || transaction.getDate().isEqual(end))) {
                System.out.println(transaction);
                found = true;
            }
        }

        if (!found) {
            System.out.printf("No transaction found from %s to %s!\n", start, end);
        }
    }

    private static void filterTransactionsByVendor(String vendor) {

        tableHeader();
        boolean found = false;

        for (Transaction transaction : transactions) {
            if (transaction.getVendor().toLowerCase().contains(vendor)) {
                System.out.println(transaction);
                found = true;
            }
        }

        if (!found) {
            System.out.printf("No vendor found matching your search: %s!\n", vendor);
        }
    }

    private static void customSearch(Scanner scanner) {

        System.out.println();
        System.out.println("Enter start date (yyyy-MM-dd) blank=none: ");
        LocalDate searchStartDate = parseDate(scanner.nextLine().trim());

        System.out.println("Enter end date (yyyy-MM-dd) blank=none: ");
        LocalDate searchEndDate = parseDate(scanner.nextLine().trim());

        System.out.println("Enter the description of the transaction blank=none: ");
        String searchDescription = scanner.nextLine().trim();

        System.out.println("Enter the vendor/payee of the transaction blank=none: ");
        String searchVendor = scanner.nextLine().trim();

        System.out.println("Enter the payment amount of the transaction blank=none: ");
        String amountInput = scanner.nextLine().trim();
        Double searchAmount = parseDouble(amountInput);

        boolean found = false;

        if (searchStartDate == null && searchEndDate == null && searchDescription.isEmpty()
                && searchVendor.isEmpty() && searchAmount == null) {
            tableHeader();
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
                found = true;
            }

            if (!found) {
                System.out.println("Returning all transactions, No transaction history found!");
            }

        } else {
            // Performs like a filter action. For each transaction, it is defaulted to print out,
            // if any conditions matches it will filter out the transaction.
            for (Transaction transaction : transactions) {
                boolean isMatch = true;

                if (searchStartDate != null && transaction.getDate().isBefore(searchStartDate)) {
                    isMatch = false;
                }

                if (searchEndDate != null && transaction.getDate().isAfter(searchEndDate)) {
                    isMatch = false;
                }

                if (!searchDescription.isEmpty() &&
                        !transaction.getDescription().toLowerCase().contains(searchDescription.toLowerCase())) {
                    isMatch = false;
                }

                if (!searchVendor.isEmpty()
                        && !transaction.getVendor().toLowerCase().contains(searchVendor.toLowerCase())) {
                    isMatch = false;
                }

                if (searchAmount != null && transaction.getAmount() != searchAmount) {
                    isMatch = false;
                }

                if (isMatch) {
                    tableHeader();
                    System.out.println(transaction);
                    found = true;
                }
            }

            if (!found) {
                tableHeader();
                System.out.println("No transaction found based on your search criteria");
            }

        }

    }

    private static void tableHeader() {
        System.out.println();
        System.out.println("Date        Time       Description                     Vendor                    Amount");
        System.out.println("---------------------------------------------------------------------------------------");
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {

        if (s.isEmpty()) {
            return null;
        } else {
            return LocalDate.parse(s, DATE_FMT);
        }
    }

    private static Double parseDouble(String s) {

        if (s == null || s.isEmpty()) {
            return null;
        } else {
            return Double.parseDouble(s);
        }
    }
}
