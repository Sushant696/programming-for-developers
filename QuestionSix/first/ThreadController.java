package QuestionSix.first;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadController {

    private final NumberPrinter printer;
    private final Lock lock;
    private final Condition zeroCondition;
    private final Condition oddCondition;
    private final Condition evenCondition;
    private int currentNumber;
    private final int maxNumber;
    private boolean isZeroTurn;

    public ThreadController(int n) {
        this.printer = new NumberPrinter();
        this.lock = new ReentrantLock();
        this.zeroCondition = lock.newCondition();
        this.oddCondition = lock.newCondition();
        this.evenCondition = lock.newCondition();
        this.currentNumber = 1;
        this.maxNumber = n;
        this.isZeroTurn = true;
    }

    public void printZero() throws InterruptedException {
        lock.lock();
        try {
            while (currentNumber <= maxNumber) {
                if (!isZeroTurn) {
                    zeroCondition.await();
                }
                if (currentNumber > maxNumber) {
                    break;
                }
                printer.printZero();
                isZeroTurn = false;
                if (currentNumber % 2 == 1) {
                    oddCondition.signal();
                } else {
                    evenCondition.signal();
                }
            }
            // Notify all threads to exit
            oddCondition.signalAll();
            evenCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void printOdd() throws InterruptedException {
        lock.lock();
        try {
            while (currentNumber <= maxNumber) {
                if (isZeroTurn || currentNumber % 2 == 0) {
                    oddCondition.await();
                }
                if (currentNumber > maxNumber) {
                    break;
                }
                printer.printOdd(currentNumber);
                currentNumber++;
                isZeroTurn = true;
                zeroCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void printEven() throws InterruptedException {
        lock.lock();
        try {
            while (currentNumber <= maxNumber) {
                if (isZeroTurn || currentNumber % 2 == 1) {
                    evenCondition.await();
                }
                if (currentNumber > maxNumber) {
                    break;
                }
                printer.printEven(currentNumber);
                currentNumber++;
                isZeroTurn = true;
                zeroCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}

class Main {

    public static void main(String[] args) {
        int n = 5;
        ThreadController controller = new ThreadController(n);

        Thread zeroThread = new Thread(() -> {
            try {
                controller.printZero();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread oddThread = new Thread(() -> {
            try {
                controller.printOdd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread evenThread = new Thread(() -> {
            try {
                controller.printEven();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        zeroThread.start();
        oddThread.start();
        evenThread.start();

        try {
            zeroThread.join();
            oddThread.join();
            evenThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

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
