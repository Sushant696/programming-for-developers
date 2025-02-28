package QuestionSix.first;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// NumberPrinter class (provided, cannot be modified)
class NumberPrinter {

    public void printZero() {
        System.out.print("0");
    }

    public void printEven(int num) {
        System.out.print(num);
    }

    public void printOdd(int num) {
        System.out.print(num);
    }
}

// ThreadController class to coordinate the threads
class ThreadController {

    private final NumberPrinter printer = new NumberPrinter(); // Printer instance
    private final Lock lock = new ReentrantLock(); // Lock for synchronization
    private final Condition zeroTurn = lock.newCondition(); // Condition for ZeroThread
    private final Condition oddTurn = lock.newCondition(); // Condition for OddThread
    private final Condition evenTurn = lock.newCondition(); // Condition for EvenThread
    private int currentNumber = 1; // Tracks the current number to print
    private final int maxNumber; // Maximum number to print up to
    private boolean isZeroTurn = true; // Indicates if it's ZeroThread's turn

    public ThreadController(int n) {
        this.maxNumber = n; // Initialize the maximum number
    }

    // Method for ZeroThread
    public void printZero() throws InterruptedException {
        lock.lock();
        try {
            while (currentNumber <= maxNumber) { // Continue until all numbers are printed
                if (!isZeroTurn) {
                    zeroTurn.await(); // Wait if it's not ZeroThread's turn
                }
                if (currentNumber > maxNumber) {
                    break; // Exit if all numbers are printed
                }
                printer.printZero(); // Print 0
                isZeroTurn = false; // Switch to OddThread or EvenThread
                if (currentNumber % 2 == 1) {
                    oddTurn.signal(); // Signal OddThread if the next number is odd
                } else {
                    evenTurn.signal(); // Signal EvenThread if the next number is even
                }
            }
            // Notify all threads to exit after completion
            oddTurn.signalAll();
            evenTurn.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Method for OddThread
    public void printOdd() throws InterruptedException {
        lock.lock();
        try {
            while (currentNumber <= maxNumber) { // Continue until all numbers are printed
                if (isZeroTurn || currentNumber % 2 == 0) {
                    oddTurn.await(); // Wait if it's not OddThread's turn
                }
                if (currentNumber > maxNumber) {
                    break; // Exit if all numbers are printed
                }
                printer.printOdd(currentNumber); // Print the odd number
                currentNumber++; // Move to the next number
                isZeroTurn = true; // Switch back to ZeroThread
                zeroTurn.signal(); // Signal ZeroThread
            }
        } finally {
            lock.unlock();
        }
    }

    // Method for EvenThread
    public void printEven() throws InterruptedException {
        lock.lock();
        try {
            while (currentNumber <= maxNumber) { // Continue until all numbers are printed
                if (isZeroTurn || currentNumber % 2 == 1) {
                    evenTurn.await(); // Wait if it's not EvenThread's turn
                }
                if (currentNumber > maxNumber) {
                    break; // Exit if all numbers are printed
                }
                printer.printEven(currentNumber); // Print the even number
                currentNumber++; // Move to the next number
                isZeroTurn = true; // Switch back to ZeroThread
                zeroTurn.signal(); // Signal ZeroThread
            }
        } finally {
            lock.unlock();
        }
    }
}

// Main class to start the threads
class Main {

    public static void main(String[] args) {
        int n = 5; // Maximum number to print
        ThreadController controller = new ThreadController(n); // Create controller

        // Create and start ZeroThread
        Thread zeroThread = new Thread(() -> {
            try {
                controller.printZero();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Create and start OddThread
        Thread oddThread = new Thread(() -> {
            try {
                controller.printOdd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Create and start EvenThread
        Thread evenThread = new Thread(() -> {
            try {
                controller.printEven();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Start all threads
        zeroThread.start();
        oddThread.start();
        evenThread.start();

        // Wait for all threads to finish
        try {
            zeroThread.join();
            oddThread.join();
            evenThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
