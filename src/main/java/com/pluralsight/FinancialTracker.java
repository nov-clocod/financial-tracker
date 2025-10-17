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
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\033[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String WHITE = "\u001B[97m";
    private static final String GRAY = "\u001B[90m";
    private static final String ORANGE = "\u001B[38;5;208m";
    private static final String BOLD_YELLOW = "\u001B[1m\u001B[93m";
    private static final String CYAN = "\u001B[36m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";

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
            System.out.println(GREEN + "D) Add Deposit");
            System.out.println(RED + "P) Make Payment (Debit)");
            System.out.println(ORANGE + "L) Ledger" + RESET);
            System.out.println("X) Exit");
            System.out.println(BOLD_YELLOW + "==============================================" + RESET);
            System.out.println("Choose an option to get the dough started:");
            String input = scanner.nextLine().trim();
            System.out.println();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> {
                    running = false;
                    System.out.println(ORANGE + "Thanks for using our app! We hope to see you pizza happy again!.");
                }
                default -> System.out.println("That's not very doughy, try again\n");
            }
        }
        scanner.close();
    }
    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */
    /**
     * Load transactions from FILE_NAME.
     * If the file doesn’t exist, create an empty one so that future writes succeed.
     * Each line in the file looks like: date|time|description|vendor|amount
     * @param fileName the name of the transactions file to load
     */
    public static void loadTransactions(String fileName) {

        try {
            BufferedReader myReader = new BufferedReader(new FileReader(fileName));
            File transactionsFile = new File(fileName);

            //Creates new file if the file doesn't exist
            //Reads the transactions from the file if file exists
            if (transactionsFile.createNewFile()) {
                System.out.println(GREEN + "Fresh file created!" + RESET);
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
            System.out.println(RED + "Oops, something went wrong. Contact the Piz-xperts" + RESET);
            System.out.println(exception.getMessage());
        }

    }
    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */
    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
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
            double userDepositAmount = Double.parseDouble(stringInputDepositAmount);

            if (userDepositAmount <= 0) {
                System.out.println(RED + "\nSorry, only positives are allowed in this place\n" + RESET);
                return;
            }

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
            System.out.println(GREEN + "Sale recorded!\n" + RESET);
            myWriter.close();

        } catch (Exception exception) {
            System.out.println(RED + "Uh-oh, the oven is not baking to the file" + RESET);
            System.out.println(exception.getMessage() + "\n");
        }

    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
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
            double userInputPaymentAmount = Double.parseDouble(stringInputDepositAmount);

            if (userInputPaymentAmount <= 0) {
                System.out.println(RED + "\nSorry, only positives are allowed in this place\n" + RESET);
                return;
            }

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
            System.out.println(RED + "Payment recorded! \n" + RESET);
            myWriter.close();

        } catch (Exception exception) {
            System.out.println(RED + "Uh-oh, the oven is not baking to the file" + RESET);
            System.out.println(exception.getMessage() + "\n");
        }

    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println(BOLD_YELLOW + "==============" + ORANGE + "[ " + BOLD + ORANGE + "Ledger Menu \uD83C\uDF55 ]" + BOLD_YELLOW + "===============");
            System.out.println(BLUE + "A) All");
            System.out.println(GREEN + "D) Deposits");
            System.out.println(RED + "P) Payments");
            System.out.println(PURPLE + "R) Reports" + RESET);
            System.out.println("H) Home");
            System.out.println(BOLD_YELLOW + "===============================================" + RESET);
            System.out.println("Which slice would you like to check today?");
            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> {
                    running = false;
                    System.out.println();
                }
                default -> System.out.println(ORANGE + "That slice isn't ready yet, choose another\n" + RESET);
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
                if (transaction.getAmount() > 0 ) {
                    System.out.println(GREEN + transaction + RESET);
                } else {
                    System.out.println(RED + transaction + RESET);
                }
                found = true;
            }


            if (!found) {
                System.out.println(WHITE + "No transaction history found! Go make your first deposit or payment!\n" + RESET);
            } else {
                System.out.println();
            }

        } catch (Exception exception) {
            System.out.println(RED + "Uh-oh, we can't find dough file" + RESET);
            System.out.println(exception.getMessage() + "\n");
        }
    }

    private static void displayDeposits() {

        try {
            tableHeader();
            boolean found = false;

            for (Transaction transaction : transactions) {
                if (transaction.getAmount() > 0) {
                    System.out.println(GREEN + transaction);
                    found =true;
                }
            }

            System.out.println();

            if (!found) {
                System.out.println(WHITE + "No sales history found! Go make your first sale!\n" + RESET);
            }

        } catch (Exception exception) {
            System.out.println(RED + "Uh-oh, we can't find dough file" + RESET);
            System.out.println(exception.getMessage() + "\n");
        }

    }

    private static void displayPayments() {

        try {
            tableHeader();
            boolean found = false;

            for (Transaction transaction : transactions) {
                if (transaction.getAmount() < 0) {
                    System.out.println(RED + transaction + RESET);
                    found = true;
                }
            }

            System.out.println();

            if (!found) {
                System.out.println(WHITE + "No payment history found! Woohoo!, no vendor payments!\n" + RESET);
            }

        } catch (Exception exception) {
            System.out.println(RED + "Uh-oh, we can't find dough file" + RESET);
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
            System.out.println(BOLD_YELLOW + "==============" + PURPLE + "[ Reports Oven \uD83D\uDD25 ]" + BOLD_YELLOW + "===============");
            System.out.println(RED + "1) Month To Date");
            System.out.println(ORANGE + "2) Previous Month");
            System.out.println(RED + "3) Year To Date");
            System.out.println(ORANGE + "4) Previous Year");
            System.out.println(RED + "5) Search by Vendor");
            System.out.println(CYAN + "6) Custom Search" + RESET);
            System.out.println("0) Back");
            System.out.println(BOLD_YELLOW + "===============================================" + RESET);
            System.out.println("Which pizza report would you like to bake today?");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> monthToDateReport();
                case "2" -> previousMonthReport();
                case "3" -> yearToDateReport();
                case "4" -> previousYearReport();
                case "5" -> vendorSearch(scanner);
                case "6" -> customSearch(scanner);
                case "0" -> {
                    running = false;
                    System.out.println();
                }
                default -> System.out.println(ORANGE + "That report’s still rising, try a different one\n" + RESET);
            }
        }
    }

    /**
     * Prompts the user to enter a vendor name and filters transactions
     * and displays only transactions matching to the vendor
     * @param scanner captures the user input from the user
     */
    private static void vendorSearch(Scanner scanner) {
        System.out.println("Enter the vendor's name: ");
        String inputVendor = scanner.nextLine().trim();

        filterTransactionsByVendor(inputVendor);
    }

    /**
     * Generates a report of all transactions of the previous year
     * Determines the first day and last day of the previous year
     * Each date is then passed into the helper method for date filtering
     */
    private static void previousYearReport() {
        LocalDate firstDayOfLastYear = LocalDate.now().minusYears(1).withDayOfYear(1);
        LocalDate lastDayOfLastYear = LocalDate.now().withDayOfYear(1).minusDays(1);

        filterTransactionsByDate(firstDayOfLastYear, lastDayOfLastYear);
    }

    /**
     * Generates a report of all transactions from the start of the current year to today
     * Determines the first day of the current year and current date
     * Each date is then passed into the helper method for date filtering
     */
    private static void yearToDateReport() {
        LocalDate firstDayOfThisYear = LocalDate.now().withDayOfYear(1);
        LocalDate today = LocalDate.now();

        filterTransactionsByDate(firstDayOfThisYear, today);
    }

    /**
     * Generates a report of all transactions of the previous month.
     * Determines the first day and last day of the previous month.
     * Each date is then passed into the helper method for date filtering
     */
    private static void previousMonthReport() {
        LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = LocalDate.now().withDayOfMonth(1).minusDays(1);

        filterTransactionsByDate(firstDayOfLastMonth, lastDayOfLastMonth);
    }

    /**
     * Generates a report of all transactions from the first day of the current month to today
     * Determines the first day of the current month and current date
     * Each date is then passed into the helper method for date filtering
     */
    private static void monthToDateReport() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();

        filterTransactionsByDate(firstDayOfMonth, today);
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */

    /**
     * Filters and displays transactions within a specific date range.
     * Compares each transaction date to the provided parameter dates
     * @param start receives a LocalDate variable in the date format yyyy-MM-dd
     *              to start the range from
     * @param end receives a LocalDate variable in the date format yyyy-MM-dd
     *            to the end the range
     */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {

        tableHeader();
        boolean found = false;
        double reportSum = 0;

        for (Transaction transaction : transactions) {
            if (!transaction.getDate().isAfter(end) && !transaction.getDate().isBefore(start)) {
                if (transaction.getAmount() > 0) {
                    System.out.println(GREEN + transaction + RESET);
                    reportSum += transaction.getAmount();
                } else {
                    System.out.println(RED + transaction + RESET);
                    reportSum -= transaction.getAmount();
                }
                found = true;
            }
        }

        if (found) {
            if (reportSum > 0) {
                System.out.println("---------------------------------------------------------------------------------------");
                System.out.printf(BOLD + GREEN + "Total profit for this report: %.2f!\n" + RESET, reportSum);
            } else {
                System.out.printf(BOLD+ RED + "Total loss for this report: %.2f\n" + RESET, reportSum);
            }
        } else {
            System.out.printf(WHITE + "No transaction found from %s to %s!\n" + RESET, start, end);
        }
    }

    /**
     * Filters and displays transaction based on the parameter passed into the method
     * @param vendor receives as a String to pass into the condition to filter
     */
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
            System.out.printf(WHITE + "No vendor found matching your search: %s!\n" + RESET, vendor);
        }
    }

    /**
     * Prompts the user for input on dates in the format yyyy-MM-dd, description,
     * vendor, and the amount of the transaction
     * Performs a search in transactions ArrayList based on user inputs.
     * For each transaction, it is defaulted to print out unless any conditions
     * matches, and it will filter out the transaction.
     */
    private static void customSearch(Scanner scanner) {

        /*Each search criteria is optional, if none is entered all transactions
        will be printed. If there are criteria, only those transactions with
        the criteria will be printed*/
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

            System.out.println("Enter the amount of the transaction blank=none: ");
            String amountInput = scanner.nextLine().trim();
            Double searchAmount = parseDouble(amountInput);

            boolean found = false;
            boolean hasCriteria = (searchStartDate != null ||
                    searchEndDate != null ||
                    !searchDescription.isEmpty() ||
                    !searchVendor.isEmpty() ||
                    searchAmount != null);

            tableHeader();

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
                System.out.println(WHITE + "\nNo search criteria entered. Returning all transactions" + RESET);
            }

            if (hasCriteria && !found) {
                System.out.println(WHITE + "\nNo transactions found based on your search criteria" + RESET);
            }

        } catch (Exception exception) {
            System.out.println(RED + "Error occurred when checking your dates, are you sure the dates are in human language?" + RESET);
            System.out.println(exception.getMessage());
        }
    }

    /* ------------------------------------------------------------------
       Extra helpers
       ------------------------------------------------------------------ */

    /**
     * Displays welcome message, today's date, and account balance
     */
    private static void displayWelcomeMessage() {
        double totalBalance = 0;

        for (Transaction transaction : transactions) {
            totalBalance += transaction.getAmount();
        }

        System.out.println();
        System.out.println(RED + BOLD + "        \uD83C\uDF55  Welcome to PizzaLedger  \uD83C\uDF55");
        System.out.println(BOLD_YELLOW + "==============================================");
        System.out.println(ORANGE + "Manage your pizza shop sales and purchases");
        System.out.println("with reports where every slice counts!\n" + RESET);
        System.out.println("Today's Date: " + LocalDate.now());
        if (totalBalance > 0) {
            System.out.printf(GREEN + "Current Balance: $%.2f\n\n" + RESET, totalBalance);
        } else if (totalBalance == 0) {
            System.out.printf("Current Balance: $%.2f\n\n", totalBalance);
        } else {
            System.out.printf(RED + "Current Balance: $%.2f\n\n" + RESET, totalBalance);
        }
    }

    /**
     * Displays welcome art
     */
    private static void displayWelcomeArt() {
        String characterToRepeat = "\\";

        String pizzaStore =
                WHITE + "   ______________________________________________\n" +
                        "  /      _ _ - -                         __--    \\\n" +
                        " /  _-               _ -  _ -    _-               \\\n" +
                        "/__________________________________________________\\\n" + RESET +

                        WHITE + " |       __________________________________       |\n" +
                        " |      /" + GRAY + characterToRepeat.repeat(35) + WHITE + "      |\n" +
                        " |     /" + GRAY + characterToRepeat.repeat(37) + WHITE + "     |\n" +
                        " |  __ " + RED + "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU" + RESET + " __  |\n" +
                        WHITE + " | |%%| |  ___________________   _________ | |%%| |\n" +
                        " | |%%| | | " + BOLD_YELLOW + "P | I | Z | Z | A" + WHITE + " |  || " + ORANGE + "_ _" + WHITE + " || | |%%| |\n" +
                        " | |%%| | |   |   |   |   |   |  ||" + ORANGE + "|_|_|" + WHITE + "|| | |%%| |\n" +
                        " | |%%| | |___|___|___|___|___|  ||" + RED + "|_|_|" + WHITE + "|| | |%%| |\n" +
                        " | ==== | |%%%|%%%|%%%|%%%|%%%|  ||     || | ==== |\n" +
                        " |      | |%%%|%%%|%%%|%%%|%%%|  ||o    || |      |\n" +
                        " |______| =====================  ||     || |______|\n" +
                        GRAY + "________" + WHITE + "|________________________||_____||_|" + GRAY + "________\n" +
                        GRAY + "_________________________________/_______\\__________" + RESET;

        System.out.println(pizzaStore);
    }

    /**
     * Displays the table header for transactions/reports
     */
    private static void tableHeader() {
        System.out.println();
        System.out.println("Date        Time       Description                     Vendor                    Amount");
        System.out.println("---------------------------------------------------------------------------------------");
    }
    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */

    /**
     * Parses a string in the yyyy-MM-dd date format to return null or
     * as a LocalDate object
     * @param s the string to parse
     * @return a LocalDate object or null if the string is empty
     */
    private static LocalDate parseDate(String s) {

        if (s.isEmpty()) {
            return null;
        } else {
            return LocalDate.parse(s, DATE_FMT);
        }
    }

    /**
     * Parses a string into a Double value
     * @param s the string to parse
     * @return the string as a Double value or null if the string is empty
     */
    private static Double parseDouble(String s) {

        if (s == null || s.isEmpty()) {
            return null;
        } else {
            return Double.parseDouble(s);
        }
    }
}
