package com.pluralsight;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

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
            Collections.sort(transactions);
            displayWelcomeArt();
            displayWelcomeMessage();
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.println("==============================================");
            System.out.println("Choose an option to get the dough started:");
            String input = scanner.nextLine().trim();
            System.out.println();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> {
                    running = false;
                    System.out.println("Thanks for using our app! We hope to see you pizza happy again!.");
                }
                default -> System.out.println("That's not very doughy, try again\n");
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
            File transactionsFile = new File(fileName);

            if (transactionsFile.createNewFile()) {
                System.out.println("Fresh file created!");
            } else {
                String line;

                while ((line = myReader.readLine()) != null) {
                    String[] section = line.split("\\|");
                    LocalDate transactionDate = LocalDate.parse(section[0], DATE_FMT);
                    LocalTime transactionTime = LocalTime.parse(section[1], TIME_FMT);
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
            System.out.println("Oops, something went wrong. Contact the Piz-xperts");
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
            LocalDateTime userInputDepositDateTime = LocalDateTime.parse(scanner.nextLine().trim(), DATETIME_FMT);

            System.out.println("Enter the description of the order item: ");
            String userDepositDescription = scanner.nextLine().trim();

            System.out.println("Enter the customer's name: ");
            String userDepositVendor = scanner.nextLine().trim();

            System.out.println("Enter the total price (positive number): ");
            String stringInputDepositAmount = scanner.nextLine().trim();
            double userDepositAmount = parseDouble(stringInputDepositAmount);

            LocalDate userDepositDate = userInputDepositDateTime.toLocalDate();
            LocalTime userDepositTime = userInputDepositDateTime.toLocalTime();

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

            System.out.println();
            System.out.println("Sale recorded!\n");
            myWriter.close();

        } catch (Exception exception) {
            System.out.println("Uh-oh, the oven is not baking to the file");
            System.out.println(exception.getMessage() + "\n");
        }

    }

    private static void addPayment(Scanner scanner) {

        try {
            BufferedWriter myWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            System.out.println("Enter the date with time in this format (yyyy-MM-dd HH:mm:s): ");
            LocalDateTime userInputPaymentDateTime = LocalDateTime.parse(scanner.nextLine().trim(), DATETIME_FMT);

            System.out.println("Enter the description of the payment to vendor: ");
            String userPaymentDescription = scanner.nextLine().trim();

            System.out.println("Enter the vendor's name: ");
            String userPaymentVendor = scanner.nextLine().trim();

            System.out.println("Enter the total price (positive number): ");
            String stringInputDepositAmount = scanner.nextLine().trim();
            double userPaymentAmount = parseDouble("-" + stringInputDepositAmount);

            LocalDate userPaymentDate = userInputPaymentDateTime.toLocalDate();
            LocalTime userPaymentTime = userInputPaymentDateTime.toLocalTime();

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

            System.out.println();
            System.out.println("Payment recorded! \n");
            myWriter.close();

        } catch (Exception exception) {
            System.out.println("Uh-oh, the oven is not baking to the file");
            System.out.println(exception.getMessage() + "\n");
        }

    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            //Java can't process an emoji because it is larger than 16bit so they use a surrogate
            //pair to make it happen in the background
            System.out.println("==============[ Ledger Menu \uD83C\uDF55 ]===============");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.println("===============================================");
            System.out.println("Which slice would you like to check today?");
            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("That slice isn't ready yet, choose another\n");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {

        try {
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

        } catch (Exception exception) {
            System.out.println("Uh-oh, we can't find dough file");
            System.out.println(exception.getMessage() + "\n");
        }
    }

    private static void displayDeposits() {

        try {
            tableHeader();
            boolean found = false;

            for (Transaction transaction : transactions) {
                if (transaction.getAmount() > 0) {
                    System.out.println(transaction);
                    found =true;
                }
            }

            System.out.println();

            if (!found) {
                System.out.println("No sales history found! Go make your first sale!\n");
            }

        } catch (Exception exception) {
            System.out.println("Uh-oh, we can't find dough file");
            System.out.println(exception.getMessage() + "\n");
        }

    }

    private static void displayPayments() {

        try {
            tableHeader();
            boolean found = false;

            for (Transaction transaction : transactions) {
                if (transaction.getAmount() < 0) {
                    System.out.println(transaction);
                    found = true;
                }
            }

            System.out.println();

            if (!found) {
                System.out.println("No payment history found! Woohoo!, no vendor payments!\n");
            }

        } catch (Exception exception) {
            System.out.println("Uh-oh, we can't find dough file");
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
            System.out.println("==============[ Reports Oven \uD83D\uDD25 ]===============");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");
            System.out.println("===============================================");
            System.out.println("Which pizza report would you like to bake today?");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> monthToDateReport();
                case "2" -> previousMonthReport();
                case "3" -> yearToDateReport();
                case "4" -> previousYearReport();
                case "5" -> vendorSearch(scanner);
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("That report’s still rising, try a different one\n");
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
            if (!transaction.getDate().isAfter(end) && !transaction.getDate().isBefore(start)) {
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
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                System.out.println(transaction);
                found = true;
            }
        }

        if (!found) {
            System.out.printf("No vendor found matching your search: %s!\n", vendor);
        }
    }

    private static void customSearch(Scanner scanner) {

        try {
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
            boolean hasCriteria = (searchStartDate != null ||
                    searchEndDate != null ||
                    !searchDescription.isEmpty() ||
                    !searchVendor.isEmpty() ||
                    searchAmount != null);

            tableHeader();
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
                        !transaction.getDescription().equalsIgnoreCase(searchDescription)) {
                    isMatch = false;
                }

                if (!searchVendor.isEmpty()
                        && !transaction.getVendor().equalsIgnoreCase(searchVendor)) {
                    isMatch = false;
                }

                if (searchAmount != null && transaction.getAmount() != searchAmount) {
                    isMatch = false;
                }

                if (isMatch) {
                    System.out.println(transaction);
                    found = true;
                }
            }

            if (!hasCriteria) {
                System.out.println("\nNo search criteria entered. Returning all transactions");
            }

            if (hasCriteria && !found) {
                System.out.println("\nNo transactions found based on your search criteria");
            }

        } catch (Exception exception) {
            System.out.println("Error occurred when checking your dates, are you sure the dates are in human language?");
            System.out.println(exception.getMessage());
        }
    }

    /* ------------------------------------------------------------------
       Extra helpers
       ------------------------------------------------------------------ */
    //Since I added an extra feature, I want to also customize the welcoming message while not taking much space
    //in the main code
    private static void displayWelcomeMessage() {
        double totalBalance = 0;

        for (Transaction transaction : transactions) {
            totalBalance += transaction.getAmount();
        }

        System.out.println();
        System.out.println("        \uD83C\uDF55  Welcome to PizzaLedger  \uD83C\uDF55");
        System.out.println("==============================================");
        System.out.println("Manage your pizza shop sales and purchases");
        System.out.println("with reports where every slice counts!\n");
        System.out.println("Today's Date: " + LocalDate.now());
        System.out.printf("Current Balance: $%.2f\n\n", totalBalance);
    }

    //This is an extra feature that I didn't want to directly add into the main method that takes up a lot of space
    private static void displayWelcomeArt() {
        String characterToRepeat = "\\";

        String pizzaStore = "   ______________________________________________\n" +
                "  /      _ _ - -                         __--    \\\n" +
                " /  _-               _ -  _ -    _-               \\\n" +
                "/__________________________________________________\\\n" +
                " |       __________________________________       |\n" +
                " |      /" + characterToRepeat.repeat(35) + "      |\n" +
                " |     /" + characterToRepeat.repeat(37) + "     |\n" +
                " |  __ UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU __  |\n" +
                " | |%%| |  ___________________   _________ | |%%| |\n" +
                " | |%%| | | P | I | Z | Z | A |  || _ _ || | |%%| |\n" +
                " | |%%| | |   |   |   |   |   |  |||_|_||| | |%%| |\n" +
                " | |%%| | |___|___|___|___|___|  |||_|_||| | |%%| |\n" +
                " | ==== | |%%%|%%%|%%%|%%%|%%%|  ||     || | ==== |\n" +
                " |      | |%%%|%%%|%%%|%%%|%%%|  ||o    || |      |\n" +
                " |______| =====================  ||     || |______|\n" +
                "________|________________________||_____||_|________\n" +
                "_________________________________/_______\\__________";
        System.out.println(pizzaStore);
    }

    //The table header is used many times through the code, so instead of printing out this I made a method of it
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
