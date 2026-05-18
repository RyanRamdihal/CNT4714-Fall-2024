/* Name: Ryan Ramdihal
 Course: CNT 4714 Fall 2024
 Assignment title:
 Project 2 – Synchronized/Cooperating Threads – A Banking Simulation
 Due Date: September 22, 2024
*/
import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 
import java.util.concurrent.locks.Condition; // Import for condition variables
import java.util.concurrent.locks.Lock; // Import for lock mechanisms
import java.util.concurrent.locks.ReentrantLock; 

// Class representing a bank account
class BankAccount {
    private int currentBalance; // Current balance in the account
    private static int transactionNumber = 0; // Static counter for transactions
    private Lock lock = new ReentrantLock(); // Lock for synchronizing access
    private Condition overDraft = lock.newCondition(); // handling overdrafts
    private static final int DEPOSIT_ALERT_LEVEL = 450; // Threshold for deposit alerts
    private static final int WITHDRAWAL_ALERT_LEVEL = 90; // Threshold for withdrawal alerts
    private int auditTransactionNumber = 0; // Counter for audit transactions
    private int TRauditTransactionNumber = 0; // Counter for audit transactions

    
    private String bankAccountID; // Unique identifier for the account

    // Constructor to initialize the account with a unique ID
    public BankAccount(String id) {
        currentBalance = 0; // Set initial balance to zero
        this.bankAccountID = id; // Assign the unique ID to the account
    }

    // Method to deposit money into the bank account
    public void Deposits(String name) {
        int deposit = (int) (Math.random() * 600 + 1); //  between $1 and $600
        lock.lock(); // Acquire the lock before modifying the balance
        try {
            currentBalance += deposit; // Update the balance
            transactionNumber++; // Increment transaction counter
            System.out.println(name + " deposits $" + deposit + " into Account " + bankAccountID + " - Balance: $" + currentBalance + " [Transaction #" + transactionNumber + "]\n");
            if (deposit >= DEPOSIT_ALERT_LEVEL) { // Check if deposit exceeds alert threshold
                System.out.println("\n* * * Flagged Transaction: Deposit by " + name + " in Account " + bankAccountID + " exceeded $" + DEPOSIT_ALERT_LEVEL + ". See log.\n\n");
                flag(deposit, name, "D"); // Log flagged transaction
            }
            overDraft.signalAll(); // Signal all threads waiting for overdraft condition
        } catch (Exception e) {
            System.out.println("Exception during deposit");
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    // Method to withdraw money from the bank account
    public void Withdraws(String name) {
        int withdraw = (int) (Math.random() * 99 + 1); // between $1 and $99
        lock.lock(); // Acquire the lock before modifying the balance
        try {
            if (currentBalance >= withdraw) { // Check if sufficient funds are available
                currentBalance -= withdraw; // Update the balance
                transactionNumber++; // Increment transaction counter
                System.out.println(name + " withdraws $" + withdraw + " from Account " + bankAccountID + " - Balance: $" + currentBalance + " [Transaction #" + transactionNumber + "]\n");
            } else {
                System.out.println(name + " attempted to withdraw $" + withdraw + " from Account " + bankAccountID + " - Blocked due to insufficient funds.\n");
                overDraft.await(); // Wait if funds are insufficient
            }
            if (withdraw >= WITHDRAWAL_ALERT_LEVEL) { // Check if withdrawal exceeds alert threshold
                System.out.println("* * * Flagged Transaction: Withdrawal by " + name + " in Account " + bankAccountID + " exceeded $" + WITHDRAWAL_ALERT_LEVEL + ". See log.\n");
                flag(withdraw, name, "W"); // Log flagged transaction
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during withdrawal");
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    // Auditor method to display account balance and transactions since last audit
    // Auditor method to display account balance and transactions since last audit
public void AuditorMethod(BankAccount account1, BankAccount account2, String name) {
    boolean success = false; // Flag to indicate success in locking both accounts

    while (!success) {
        if (account1.lock.tryLock()) { // Try to lock the first account
            try {
                if (account2.lock.tryLock()) { // Try to lock the second account
                    try {
                        // Both accounts are locked, perform audit
                        System.out.println(name + " auditing Accounts " + account1.bankAccountID + " and " + account2.bankAccountID + 
                                           " \n JA-1 Balance: $" + account1.currentBalance + " [Transactions since last audit: " + (account1.transactionNumber - account1.auditTransactionNumber)+
                                           "\n JA-2 Balance: $" + account2.currentBalance +
                                           " [Transactions since last audit: " 
                                           + (account2.transactionNumber - account2.auditTransactionNumber) + "]\n");
                        account1.auditTransactionNumber = account1.transactionNumber; // Update audit transaction counter for account 1
                        account2.auditTransactionNumber = account2.transactionNumber; // Update audit transaction counter for account 2
                        success = true; // Mark as successful
                    } finally {
                        account2.lock.unlock(); // Release lock on second account
                    }
                }
            } finally {
                account1.lock.unlock(); // Release lock on first account
            }
        }

        if (!success) {
            // Sleep before retrying to avoid busy-waiting
            try {
                Thread.sleep((int) (Math.random() * 12000 + 1000)); // Sleep for a random time between 1-12 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}


    // Treasury method to display account balance and transactions since last audit
public void TreasuryMethod(BankAccount account1, BankAccount account2, String name) {
    boolean success = false; // Flag to indicate success in locking both accounts

    while (!success) {
        if (account1.lock.tryLock()) { // Try to lock the first account
            try {
                if (account2.lock.tryLock()) { // Try to lock the second account
                    try {
                        // Both accounts are locked, perform audit
                        System.out.println(name + " (Treasury) auditing Accounts " + account1.bankAccountID + " and " + account2.bankAccountID + 
                        " \n JA-1 Balance: $" + account1.currentBalance + " [Transactions since last audit: " + (account1.transactionNumber - account1.TRauditTransactionNumber)+
                        "\n JA-2 Balance: $" + account2.currentBalance +
                        " [Transactions since last audit: " 
                        + (account2.transactionNumber - account2.TRauditTransactionNumber) + "]\n");
                        account1.TRauditTransactionNumber = account1.transactionNumber; // Update audit transaction counter for account 1
                        account2.TRauditTransactionNumber = account2.transactionNumber; // Update audit transaction counter for account 2
                        success = true; // Mark as successful
                    } finally {
                        account2.lock.unlock(); // Release lock on second account
                    }
                }
            } finally {
                account1.lock.unlock(); // Release lock on first account
            }
        }

        if (!success) {
            // Sleep before retrying to avoid busy-waiting
            try {
                Thread.sleep((int) (Math.random() * 12000 + 1000)); // Sleep for a random time between 1-12 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}


    // Method to transfer money between two bank accounts
    public void transfer(BankAccount targetAccount, int amount, String name) {
        boolean success = false; // Flag to indicate transfer success
        while (!success) { // Retry loop for transferring funds
            lock.lock(); // Acquire the lock for the source account
            try {
                if (targetAccount.lock.tryLock()) { // Try to acquire the lock for the target account
                    try {
                        if (currentBalance >= amount) { // Check if sufficient funds are available
                            currentBalance -= amount; // Deduct amount from source account
                            targetAccount.currentBalance += amount; // Add amount to target account
                            transactionNumber++; // Increment transaction counter
                            System.out.println(name + " transfers $" + amount + " from Account " + bankAccountID + " to Account " + targetAccount.bankAccountID + " - Source Balance: $" + currentBalance + ", Target Balance: $" + targetAccount.currentBalance + " [Transaction #" + transactionNumber + "]\n");
                            success = true; // Transfer successful
                        } else {
                            System.out.println(name + " attempted to transfer $" + amount + " from Account " + bankAccountID + " - Blocked due to insufficient funds.\n");
                            success = false; // Transfer failed due to insufficient funds
                        }
                    } finally {
                        targetAccount.lock.unlock(); // Release the lock for the target account
                    }
                } else {
                    System.out.println(name + " could not acquire lock for target account. Retrying...\n");
                    success = false; // Retry if target account is locked
                }
            } finally {
                lock.unlock(); // Release the lock for the source account
            }
            if (!success) { // Sleep before retrying if transfer failed
                try {
                    Thread.sleep((int) (Math.random() * 4500 + 500)); // Sleep for a random duration
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Handle interrupted exception
                }
            }
        }
    }

    // Method to log flagged transactions
    public void flag(int value, String transaction_thread, String transaction_type) {
        Date today = new Date(); // Get current date and time
        DateFormat frenchDateTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, Locale.FRANCE); // Set date format
        FileWriter transactionFile; // FileWriter for writing to file
        PrintWriter aPrintWriter = null; // PrintWriter for writing text to file
        StringBuilder output_to_FTF = new StringBuilder(); // StringBuilder for constructing log message
        try {
            transactionFile = new FileWriter("transactions.csv", true); // Open file in append mode
            aPrintWriter = new PrintWriter(transactionFile); // Initialize PrintWriter
            String timestampID = frenchDateTime.format(today); // Format current date and time
            if (transaction_type.equals("D")) { // Check if transaction is a deposit
                output_to_FTF.append("\n Depositor " + transaction_thread + " issued deposit of $" + value + ".00 at " + timestampID + " Transaction Number: " + transactionNumber);
            } else { // Otherwise, it's a withdrawal
                output_to_FTF.append("\n Withdrawal " + transaction_thread + " issued withdrawal of $" + value + ".00 at " + timestampID + " Transaction Number: " + transactionNumber);
            }
            aPrintWriter.print(output_to_FTF.toString()); // Write log message to file
        } catch (IOException ioException) {
            System.out.println("\nError: Problem writing to transaction file.\n");
        } finally {
            if (aPrintWriter != null) {
                aPrintWriter.close(); // Close PrintWriter
            }
        }
    }
}

// Class for handling withdrawal operations in a separate thread
class Withdraw implements Runnable {
    private static final Random RANDOM = new Random(); // Random number generator
    private BankAccount[] bankAccounts; // Array of bank accounts
    private String name; // Name of the agent

    // Constructor to initialize Withdraw instance
    public Withdraw(BankAccount[] bankAccounts, String name) {
        this.bankAccounts = bankAccounts; // Set bank accounts array
        this.name = name; // Set agent name
    }

    @Override
    public void run() {
        try {
            while (true) { // Continuous loop for withdrawal operations
                BankAccount bankAccount = bankAccounts[RANDOM.nextInt(bankAccounts.length)]; // Randomly select a bank account
                bankAccount.Withdraws(name); // Perform withdrawal operation

                int randomSleep = (int)(Math.random() * 4500 + 500); // Generate random sleep duration
                Thread.sleep(randomSleep); // Sleep before next operation
            }
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Handle thread interruption
        }
    }
}

// Class for handling deposit operations in a separate thread
class Deposit implements Runnable {
    private static final Random RANDOM = new Random(); // Random number generator
    private BankAccount[] bankAccounts; // Array of bank accounts
    private String name; // Name of the agent

    // Constructor to initialize Deposit instance
    public Deposit(BankAccount[] bankAccounts, String name) {
        this.bankAccounts = bankAccounts; // Set bank accounts array
        this.name = name; // Set agent name
    }

    @Override
    public void run() {
        try {
            while (true) { // Continuous loop for deposit operations
                BankAccount bankAccount = bankAccounts[RANDOM.nextInt(bankAccounts.length)]; // Randomly select a bank account
                bankAccount.Deposits(name); // Perform deposit operation

                int randomSleep = RANDOM.nextInt(14500) + 500; // Generate random sleep duration
                Thread.sleep(randomSleep); // Sleep before next operation
            }
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Handle thread interruption
        }
    }
}

// Class for handling auditing operations in a separate thread
class Auditor implements Runnable {
    private BankAccount[] bankAccounts; // Array of bank accounts
    private String name; // Name of the auditor

    // Constructor to initialize Auditor instance
    public Auditor(BankAccount[] bankAccounts, String name) {
        this.bankAccounts = bankAccounts; // Set bank accounts array
        this.name = name; // Set auditor name
    }

    @Override
public void run() {
    try {
        while (true) { // Continuous loop for auditing operations
            bankAccounts[0].AuditorMethod(bankAccounts[0], bankAccounts[1], name); // Audit both accounts
            Thread.sleep((int) (Math.random() * 12000 + 1000)); // Sleep before next audit
        }
    } catch (InterruptedException e) {
        System.err.println("Thread interrupted: " + e.getMessage());
        Thread.currentThread().interrupt(); // Handle thread interruption
    }
}

}


// Class for handling transfer operations between accounts in a separate thread
class TransferAgent implements Runnable {
    private static final Random RANDOM = new Random(); // Random number generator
    private BankAccount[] bankAccounts; // Array of bank accounts
    private String name; // Name of the transfer agent

    // Constructor to initialize TransferAgent instance
    public TransferAgent(BankAccount[] bankAccounts, String name) {
        this.bankAccounts = bankAccounts; // Set bank accounts array
        this.name = name; // Set agent name
    }

    @Override
    public void run() {
        try {
            while (true) { // Continuous loop for transfer operations
                BankAccount sourceAccount = bankAccounts[RANDOM.nextInt(bankAccounts.length)]; // Randomly select a source account
                BankAccount targetAccount = bankAccounts[RANDOM.nextInt(bankAccounts.length)]; // Randomly select a target account
                if (sourceAccount != targetAccount) { // Ensure source and target accounts are different
                    int amount = (int) (Math.random() * 600 + 1); // Generate a random transfer amount between $1 and $600
                    sourceAccount.transfer(targetAccount, amount, name); // Perform transfer operation
                }

                int randomSleep = (int) (Math.random() * 8000 + 500); // Generate random sleep duration
                Thread.sleep(randomSleep); // Sleep before next operation
            }
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt(); // Handle thread interruption
        }
    }
}

// Class for handling treasury auditing operations in a separate thread
class Treasury implements Runnable {
    private BankAccount[] bankAccounts; // Array of bank accounts
    private String name; // Name of the treasury auditor

    // Constructor to initialize Treasury instance
    public Treasury(BankAccount[] bankAccounts, String name) {
        this.bankAccounts = bankAccounts; // Set bank accounts array
        this.name = name; // Set auditor name
    }

    @Override
public void run() {
    try {
        while (true) { // Continuous loop for treasury auditing operations
            bankAccounts[0].TreasuryMethod(bankAccounts[0], bankAccounts[1], name); // Audit both accounts
            Thread.sleep((int) (Math.random() * 15000 + 500)); // Sleep before next audit
        }
    } catch (InterruptedException e) {
        System.err.println("Thread interrupted: " + e.getMessage());
        Thread.currentThread().interrupt(); // Handle thread interruption
    }
}

}


// Main class for executing the bank simulation

public class Bank {
    public static final int MAX_AGENTS = 19; // Maximum number of agents in the simulation

    public static void main(String[] args) {
        ExecutorService application = Executors.newFixedThreadPool(MAX_AGENTS); // Create a thread pool with a fixed number of threads

        // Redirect System.out to a file
        try {
            PrintStream out = new PrintStream(new FileOutputStream("consoletransactions.out", true), true, "UTF-8");
            System.setOut(out); // Set the standard output to the file
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace(); // Handle file-related exceptions
        }

        // Create two bank accounts with unique IDs
        BankAccount bankAccount1 = new BankAccount("JA-1");
        BankAccount bankAccount2 = new BankAccount("JA-2");
        BankAccount[] bankAccounts = { bankAccount1, bankAccount2 }; // Array of bank accounts

        try {
            // Print the initial column headings
            System.out.println("Agents\t\t\t Balance\t\t\t Transaction Number\n" +
                               "---------\t\t ---------\t\t ------------------------");

            // Execute threads for various agents
            application.execute(new Withdraw(bankAccounts, "Agent WT1")); // Start withdrawal agent 1
            application.execute(new Withdraw(bankAccounts, "Agent WT2")); // Start withdrawal agent 2
            application.execute(new Withdraw(bankAccounts, "Agent WT3")); // Start withdrawal agent 3
            application.execute(new Withdraw(bankAccounts, "Agent WT4")); // Start withdrawal agent 4
            application.execute(new Withdraw(bankAccounts, "Agent WT5")); // Start withdrawal agent 5
            application.execute(new Withdraw(bankAccounts, "Agent WT6")); // Start withdrawal agent 6
            application.execute(new Withdraw(bankAccounts, "Agent WT8")); // Start withdrawal agent 7
            application.execute(new Withdraw(bankAccounts, "Agent WT7")); // Start withdrawal agent 8
            application.execute(new Withdraw(bankAccounts, "Agent WT9")); // Start withdrawal agent 9
            application.execute(new Withdraw(bankAccounts, "Agent WT10")); // Start withdrawal agent 10

            application.execute(new Deposit(bankAccounts, "Agent DT3")); // Start deposit agent 1
            application.execute(new Deposit(bankAccounts, "Agent DT1")); // Start deposit agent 2
            application.execute(new Deposit(bankAccounts, "Agent DT4")); // Start deposit agent 3
            application.execute(new Deposit(bankAccounts, "Agent DT2")); // Start deposit agent 4
            application.execute(new Deposit(bankAccounts, "Agent DT5")); // Start deposit agent 5

            application.execute(new Auditor(bankAccounts, "AUDITOR")); // Start auditor
            application.execute(new Treasury(bankAccounts, "TREASURY AUDITOR")); // Start treasury auditor
            
            application.execute(new TransferAgent(bankAccounts, "Agent TR1")); // Start transfer agent 1
            application.execute(new TransferAgent(bankAccounts, "Agent TR2")); // Start transfer agent 2

        } catch (Exception exception) {
            exception.printStackTrace(); // Print stack trace for exceptions
        } finally {
            application.shutdown(); // Shut down the executor service
        }
    }
}
