package com.bigfilesort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Sort {
    private static final String TMPDIR_PROPERTY = "java.io.tmpdir";
    private static int STRING_OBJECT_SIZE;
    private static final int STRING_OBJECT_SIZE_64_BIT = 48;
    private static final int STRING_OBJECT_SIZE_32_BIT = 24;
    private static boolean IS_64_BIT_JVM;
    private static final String DEFAULT_PATH_TO_UNSORTED_FILE = System.getProperty(TMPDIR_PROPERTY) + "/UnsortedFile.txt";
    private static final Comparator<String> COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String s, String t1) {
            return s.compareToIgnoreCase(t1);
        }
    };
    private static final boolean DELETE_DUPLICATE_STRINGS = false;

    static {
        IS_64_BIT_JVM = true;
        String arch = System.getProperty("sun.arch.data.model");
        if (arch != null) {
            if (arch.contains("32")) {
                IS_64_BIT_JVM = false;
            }
        }
        STRING_OBJECT_SIZE = IS_64_BIT_JVM ? STRING_OBJECT_SIZE_64_BIT : STRING_OBJECT_SIZE_32_BIT;
    }

    public static void main(String[] args) {
        try {
            String path = args != null && args.length > 0 && args[0] != null ? args[0] : DEFAULT_PATH_TO_UNSORTED_FILE;
            File unsortedFile = new File(path);
            FileReader fr = new FileReader(unsortedFile);
            BufferedReader reader = new BufferedReader(fr);
            long estimatedSubFileSize = availableMemory() / 2;
            Set<File> subFileNames = new HashSet<>();

            List<String> curStringsPart = new ArrayList<>();
            long curFileSize = 0;
            String line = reader.readLine();
            while (line != null) {
                curStringsPart.add(line);
                curFileSize += (line.length() * 2 + STRING_OBJECT_SIZE);
                line = reader.readLine();
                if (curFileSize > estimatedSubFileSize || line == null) {
                    InternalQuickSort sorter = new InternalQuickSort(COMPARATOR);
                    sorter.sort(curStringsPart);
                    subFileNames.add(saveCurrentPart(curStringsPart, subFileNames.size()));
                    curStringsPart = new ArrayList<>();
                    curFileSize = 0;
                    System.gc();
                }
            }
            fr.close();

            ExternalMergeSort finalSorter = new ExternalMergeSort(unsortedFile, subFileNames, COMPARATOR, DELETE_DUPLICATE_STRINGS);
            finalSorter.sortAndSave();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File saveCurrentPart(List<String> currentPart, int partCount) throws IOException {
        String tempDir = System.getProperty(TMPDIR_PROPERTY);

        File dir = new File(tempDir);
        File filename = File.createTempFile("SortedSubFile" + partCount, ".txt", dir);
        FileWriter fileWriter = new FileWriter(filename, true);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        for (String curString : currentPart) {
            bw.write(curString + System.lineSeparator());
        }
        bw.close();
        return filename;
    }

    /**
     * Возвращает размер свободной памяти в байтах
     *
     * @return
     */
    public static long availableMemory() {
        System.gc();
        long allocatedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long freeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
        return freeMemory;
    }


}
