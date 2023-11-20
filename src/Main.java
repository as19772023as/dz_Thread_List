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

    private static final int Quantity_Text = 100;
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

        new Thread(() -> {
            counterChar(a_Queue, 'a', ATOMIC_A, maxCountA);
        }).start();

        new Thread(() -> {
            counterChar(b_Queue, 'b', ATOMIC_B, maxCountB);
        }).start();

        new Thread(() -> {
            counterChar(c_Queue, 'c', ATOMIC_C, maxCountC);
        }).start();

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void counterChar(BlockingQueue<String> queue, char x, AtomicInteger atomic, int maxVolatileCount) {
        String textMaxChar = null;
        for (int i = 0; i < Quantity_Text; i++) {
            try {
                textMaxChar = queue.take();
                for (int j = 0; j < textMaxChar.length(); j++) {
                    if (textMaxChar.charAt(j) == x) {
                        atomic.incrementAndGet();
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
            if (maxVolatileCount < atomic.get()) {
                maxVolatileCount = atomic.get();
                textMaxC = textMaxChar;
            }
            atomic.set(0);
        }
        System.out.println(x + " - в тексте максимально встречается = " + maxVolatileCount);
    }
}


