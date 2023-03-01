package com.company;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static int textSet = 10_000; /* увеличение на разряд увеличивает время в 10 раз */
    public static int length = 100_000; /* увеличение на разряд увеличивает время в 100 раз */
    /* полное время выполнения программы при textSet = 10_000, length = 100_000 составит примерно 83 часа */
    public static String letters = "abc";

    public static BlockingQueue<String> counterA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> counterB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> counterC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {

        long startTs = System.currentTimeMillis(); // start time

//        поток генератор строк, кладущий строку в каждую очередь
        Thread thread = new Thread(() -> {
            for (int i = 0; i < textSet; i++) {
                String text = generateText(letters, length);
                try {
                    counterA.put(text);
                    counterB.put(text);
                    counterC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        thread.start();

        Thread counterMaximumA = new Thread(() -> {
            searchMaxSize(counterA, 'a');
        });
        counterMaximumA.start();

        Thread counterMaximumB = new Thread(() -> {
            searchMaxSize(counterB, 'b');
        });
        counterMaximumB.start();

        Thread counterMaximumC = new Thread(() -> {
            searchMaxSize(counterC, 'c');
        });
        counterMaximumC.start();

        thread.join();
        counterMaximumA.join();
        counterMaximumB.join();
        counterMaximumC.join();

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void searchMaxSize(BlockingQueue<String> counter, char ch) {
        int everMaxSize = 0;
        String text;
        String resultText = null;
        for (int i = 0; i < textSet; i++) {
            try {
                text = counter.take();
            } catch (InterruptedException e) {
                return;
            }
            int maxSize = 0;
            for (int j = 0; j < text.length(); j++) {
                for (int k = 0; k < text.length(); k++) {
                    if (j >= k) {
                        continue;
                    }
                    boolean charFound = false;
                    for (int m = j; m < k; m++) {
                        if (text.charAt(m) == ch) {
                            charFound = true;
                            break;
                        }
                    }
                    if (!charFound && maxSize < k - j) {
                        maxSize = k - j;
                    }
                }
            }
            if (everMaxSize < maxSize) {
                everMaxSize = maxSize;
                resultText = text;
            }
        }
        if (ch == 'a') {
            System.out.println("Максимальное повторение символа 'а' найдено в следующем варианте: " + resultText.substring(0, 30) + " -> " + everMaxSize);
        } else if (ch == 'b') {
            System.out.println("Максимальное повторение символа 'b' найдено в следующем варианте: " + resultText.substring(0, 30) + " -> " + everMaxSize);
        } else {
            System.out.println("Максимальное повторение символа 'c' найдено в следующем варианте: " + resultText.substring(0, 30) + " -> " + everMaxSize);
        }
    }
}
