package com.bigfilesort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ExternalMergeSort {
    private File originalFile;
    private Set<File> sortedSubFiles;
    private Comparator<String> comparator;
    private boolean deleteDuplicated;

    public ExternalMergeSort(File originalFile, Set<File> sortedSubFiles, Comparator<String> comparator, boolean deleteDuplicated) {
        this.originalFile = originalFile;
        this.sortedSubFiles = sortedSubFiles;
        this.comparator = comparator;
        this.deleteDuplicated = deleteDuplicated;
    }

    /**
     * Сортирует слиянием построчно содержимое файлов sortedSubFiles и записывает результат в originalFile
     * @throws IOException
     */
    public void sortAndSave() throws IOException {
        List<CurrentString> listSubFiles = new ArrayList<>();
        for (File sortedFile : sortedSubFiles) {
            File sortedSubFile = new File(sortedFile.getPath());
            FileReader fr = new FileReader(sortedSubFile);
            BufferedReader reader = new BufferedReader(fr);
            CurrentString currentString = new CurrentString(reader, reader.readLine());
            listSubFiles.add(currentString);
        }
        String lastString = null;
        FileWriter fileWriter = new FileWriter(originalFile);
        BufferedWriter bw = new BufferedWriter(fileWriter);

        while (!listSubFiles.isEmpty()) {
            CurrentString min = findMinString(listSubFiles);
            if (lastString == null || (!deleteDuplicated || !lastString.equals(min.getString()))) {
                bw.write(min.getString() + System.lineSeparator());
            }
            lastString = min.getString();
            min.setString(min.getReader().readLine());
            if (min.getString() == null) {
                listSubFiles.remove(min);
            }
        }
        bw.close();
    }

    /**
     * Поиск наименьшей строки из списка текущих строк подфайлов. Использует comparator
     *
     * @param listSubFiles Список подфайлов с указанием на текущую строку
     * @return Подфайл
     */
    private CurrentString findMinString(List<CurrentString> listSubFiles) {
        CurrentString min = listSubFiles.get(0);
        for (CurrentString currentString : listSubFiles) {
            if (comparator.compare(currentString.getString(), min.getString()) < 0) {
                min = currentString;
            }
        }
        return min;
    }


    public class CurrentString {
        BufferedReader reader;

        public CurrentString(BufferedReader reader, String string) {
            this.reader = reader;
            this.string = string;
        }

        String string;

        public BufferedReader getReader() {
            return reader;
        }

        public void setReader(BufferedReader reader) {
            this.reader = reader;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}
