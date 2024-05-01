import java.util.concurrent.Semaphore;
import java.util.Random;

public class DinnerPhilosophers {

    static class Philosopher implements Runnable {
        private int id;
        private Semaphore leftFork;
        private Semaphore rightFork;
        private Random random = new Random();

        public Philosopher(int id, Semaphore leftFork, Semaphore rightFork) {
            this.id = id;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    think();
                    eat(i + 1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking");
            Thread.sleep(random.nextInt(1000)); // Симуляція часу мислення
        }

        private void eat(int mealNumber) throws InterruptedException {
            if (id == 0) {
                rightFork.acquire(); // Перший філософ бере праву виделку спочатку
                System.out.println("Philosopher " + id + " took right fork");

                leftFork.acquire(); // Потім ліву
                System.out.println("Philosopher " + id + " took left fork");
            } else {
                leftFork.acquire(); // Інші філософи беруть ліву виделку спочатку
                System.out.println("Philosopher " + id + " took left fork");

                rightFork.acquire(); // Потім праву
                System.out.println("Philosopher " + id + " took right fork");
            }

            System.out.println("Philosopher " + id + " is eating (" + mealNumber + " time)");
            Thread.sleep(random.nextInt(1000)); // Симуляція часу їжі

            if (id == 0) {
                leftFork.release(); // Перший філософ відпускає ліву виделку
                System.out.println("Philosopher " + id + " put left fork");

                rightFork.release(); // Потім праву
                System.out.println("Philosopher " + id + " put right fork");
            } else {
                rightFork.release(); // Інші філософи відпускають праву виделку
                System.out.println("Philosopher " + id + " put right fork");

                leftFork.release(); // Потім ліву
                System.out.println("Philosopher " + id + " put left fork");
            }
        }
    }

    public static void main(String[] args) {
        int numPhilosophers = 5;
        Semaphore[] forks = new Semaphore[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new Semaphore(1); // Ініціалізація виделок як семафорів
        }

        Thread[] philosophers = new Thread[numPhilosophers];
        for (int i = 0; i < numPhilosophers; i++) {
            int leftForkIndex = i;
            int rightForkIndex = (i + 1) % numPhilosophers;
            philosophers[i] = new Thread(new Philosopher(i, forks[leftForkIndex], forks[rightForkIndex]));
            philosophers[i].start(); // Запуск потоків
        }

        for (Thread philosopher : philosophers) {
            try {
                philosopher.join(); // Очікування завершення потоків
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Dinner is over.");
    }
}
