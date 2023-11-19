import java.util.Arrays;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static BlockingQueue<String> a_Queue = new ArrayBlockingQueue<>(100);
    private static volatile int maxCountA = 0;
    private static BlockingQueue<String> b_Queue = new ArrayBlockingQueue<>(100);
    private static volatile int maxCountB = 0;
    private static BlockingQueue<String> c_Queue = new ArrayBlockingQueue<>(100);
    private static volatile int maxCountC = 0;


    private static final AtomicInteger ATOMIC_A = new AtomicInteger(0);
    private static final AtomicInteger ATOMIC_B = new AtomicInteger(0);
    private static final AtomicInteger ATOMIC_C = new AtomicInteger(0);

    private static final int Quantity_Text = 10_000;
    private static final int lenght_Text = 100_000;
    private static String text = "abc";

    private static String textMaxA;
    private static String textMaxB;
    private static String textMaxC;

    public static void main(String[] args) throws InterruptedException {
        Thread threadAQueue = new Thread(() -> {
            for (int i = 0; i < Quantity_Text; i++) {
                try {
                    a_Queue.put(generateText(text, lenght_Text));
                    b_Queue.put(generateText(text, lenght_Text));
                    c_Queue.put(generateText(text, lenght_Text));
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadAQueue.start();

        Thread threadCountA = new Thread(() -> {
            String textTake;

            for (int i = 0; i < Quantity_Text; i++) {
                try {
                    textTake = a_Queue.take();
                    for (int j = 0; j < textTake.length(); j++) {
                        if (textTake.charAt(j) == 'a') {
                            ATOMIC_A.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                }
                if (maxCountA < ATOMIC_A.get()) {
                    maxCountA = ATOMIC_A.get();
                    textMaxA = textTake;
                }
                ATOMIC_A.set(0);
            }
        });
        threadCountA.start();

        Thread threadCountB = new Thread(() -> {
            String textTake;
            for (int i = 0; i < Quantity_Text; i++) {
                try {
                    textTake = b_Queue.take();
                    for (int j = 0; j < textTake.length(); j++) {
                        if (textTake.charAt(j) == 'b') {
                            ATOMIC_B.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                }
                if (maxCountB < ATOMIC_B.get()) {
                    maxCountB = ATOMIC_B.get();
                    textMaxB = textTake;
                }
                ATOMIC_B.set(0);
            }
        });
        threadCountB.start();

        Thread threadCountC = new Thread(() -> {
            String textTake;
            for (int i = 0; i < Quantity_Text; i++) {
                try {
                    textTake = c_Queue.take();
                    for (int j = 0; j < textTake.length(); j++) {
                        if (textTake.charAt(j) == 'c') {
                            ATOMIC_C.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                }
                if (maxCountC < ATOMIC_C.get()) {
                    maxCountC = ATOMIC_C.get();
                    textMaxC = textTake;
                }
                ATOMIC_C.set(0);
            }
        });
        threadCountC.start();

        Thread.sleep(30);
        System.out.println("Текст в котором максимально встречается 'а' = " + maxCountA + " раз" +
                "\nТекст в котором максимально встречается 'b' = " + maxCountB + " раз" +
                "\nТекст в котором максимально встречается 'c' = " + maxCountC + " раз");

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}

