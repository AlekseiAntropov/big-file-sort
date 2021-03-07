package com.bigfilesort;

import java.util.Comparator;
import java.util.List;

/**
 * Типовая реализация быстрой сортировки строк в памяти
 */
public class InternalQuickSort {

    List<String> strings;
    int length;
    Comparator<String> comparator;

    public InternalQuickSort(Comparator<String> comparator) {
        this.comparator = comparator;
    }

    void sort(List<String> unsortedList) {
        if (unsortedList == null || unsortedList.size() == 0) {
            return;
        }
        this.strings = unsortedList;
        this.length = unsortedList.size();
        quickSort(0, length - 1);
    }

    void quickSort(int lowerIndex, int higherIndex) {
        int i = lowerIndex;
        int j = higherIndex;
        String pivot = this.strings.get(lowerIndex + (higherIndex - lowerIndex) / 2);

        while (i <= j) {
            while (comparator.compare(this.strings.get(i), pivot) < 0) {
                i++;
            }

            while (comparator.compare(this.strings.get(j), pivot) > 0) {
                j--;
            }

            if (i <= j) {
                exchangeString(i, j);
                i++;
                j--;
            }
        }
        if (lowerIndex < j) {
            quickSort(lowerIndex, j);
        }
        if (i < higherIndex) {
            quickSort(i, higherIndex);
        }
    }

    void exchangeString(int i, int j) {
        String temp = this.strings.get(i);
        this.strings.set(i, this.strings.get(j));
        this.strings.set(j, temp);
    }

}
