package com.bigfilesort;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * Генератор текстовых файлов
 */
public class FileGenerator {
    public static final int DEFAULT_STRING_LENGTH = 20;
    public static final int DEFAULT_STRING_COUNT = 100000000;

    public static void main(String[] args) throws IOException {
            System.out.println(generateUnsortedFile(DEFAULT_STRING_COUNT, DEFAULT_STRING_LENGTH));
    }

    /**
     * Генерация строки случайной длины, но не более maxLength
     *
     * @param maxLength Максимальная длина строки
     * @return
     */
    public static String generateString(int maxLength) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        int length = Math.abs(rnd.nextInt()) % maxLength;

        StringBuilder sb = new StringBuilder(maxLength);
        for (int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    /**
     * Генерация файла состоящего из stringCount строк, каждая не более maxStringLength символов
     * @param stringCount Число строк
     * @param maxStringLength Максимальная длина каждой строки
     * @return
     * @throws IOException
     */
    private static File generateUnsortedFile(int stringCount, int maxStringLength) throws IOException {
        String property = "java.io.tmpdir";

        String tempDir = System.getProperty(property);

        File dir = new File(tempDir);
        File filename = File.createTempFile("UnsortedFile", ".txt", dir);
        FileWriter fileWriter = new FileWriter(filename, true);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for (int i = 0; i < stringCount; i++) {
            bw.write(generateString(maxStringLength) + System.lineSeparator());
        }
        bw.close();
        return filename;
    }
}
