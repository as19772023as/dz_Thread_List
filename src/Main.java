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
    private static BlockingQueue<String> b_Queue = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> c_Queue = new ArrayBlockingQueue<>(100);

    private static final int Quantity_Text = 10_000;
    private static final int lenght_Text = 100_000;
    private static String text = "abc";

    public static Thread threadTextGenerator;

    public static void main(String[] args) throws InterruptedException {
        threadTextGenerator = new Thread(() -> {
            for (int i = 0; i < Quantity_Text; i++) {
                String texts = generateText(text, lenght_Text);
                try {
                    a_Queue.put(texts);
                    b_Queue.put(texts);
                    c_Queue.put(texts);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        threadTextGenerator.start();

        Thread aChar = getThread(a_Queue, 'a');
        Thread bChar = getThread(b_Queue, 'b');
        Thread cChar = getThread(c_Queue, 'c');

        aChar.start();
        bChar.start();
        cChar.start();

        aChar.join();
        bChar.join();
        cChar.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread getThread(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            int maxChar = counterMaxChar(queue, letter);
            System.out.println(letter + " - в тексте максимально встречается = " + maxChar);
        });
    }

    public static int counterMaxChar(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int maxChar = 0;
        String text;
        try {
            while (threadTextGenerator.isAlive()) {
                text = queue.take();
                for (char c : text.toCharArray()) {
                    if (c == letter) count++;
                }
                if (count > maxChar) maxChar = count;
                count = 0;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " был прерван");
            return -1;
        }
        return maxChar;
    }
}


