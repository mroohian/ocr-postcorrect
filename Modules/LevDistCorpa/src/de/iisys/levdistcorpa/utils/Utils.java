package de.iisys.levdistcorpa.utils;

import de.iisys.levdistcorpa.trie.ConfusionMatrix;
import de.iisys.levdistcorpa.trie.IConfusionMatrix;

/**
 * Utils
 * Created by reza on 28.01.15.
 */
public class Utils {
    public static double levenshtein(String input, String word) {
        final int m = input.length();
        final int n = word.length();
        final int[][] H = new int[m + 1][n + 1]; // Extra row for labels

        // Initialize matrix
        for(int index = 0; index < m + 1; ++index) {
            // Fill table
            if (index == 0) {
                for (int index1 = 0; index1 < n + 1; index1++)
                    H[index][index1] = index1;
            } else {
                H[index][0] = index;
            }
        }

        // Fill matrix
        for (int col = 1; col <= n; col++) {
            for (int row = 1; row <= m; row++) {
                int a = H[row - 1][col - 1], ed;

                if (input.charAt(row - 1) == word.charAt(col - 1)) {
                    ed = a;
                } else {
                    int b = H[row][col - 1],
                            c = H[row - 1][col];

                    ed = 1 + Math.min(Math.min(a, b), c);
                }

                H[row][col] = ed;
            }
        }

        return H[m][n];
    }

    public static double levenshteinWeighted(String input, String word, IConfusionMatrix confusionMatrix) {
        final int m = input.length();
        final int n = word.length();
        final int[][] H = new int[m + 1][n + 1]; // Extra row for labels

        // Initialize matrix
        H[0][0] = 0;
        for(int row = 1; row <= m; row++) {
            H[row][0] = H[row-1][0] + 10 + confusionMatrix.getConfusionInsertionValue(input.charAt(row-1));
        }

        // Fill matrix
        for (int col = 1; col <= n; col++) {
            char cWord = word.charAt(col-1);
            H[0][col] = H[0][col-1] + 10 + confusionMatrix.getConfusionInsertionValue(cWord);
            for (int row = 1; row <= m; row++) {
                char cInput = input.charAt(row-1);
                int a = H[row - 1][col - 1], ed;

                if (cInput == cWord) {
                    ed = a;
                } else {
                    int b = H[row][col-1] + confusionMatrix.getConfusionRemoveValue(cInput),
                            c = H[row-1][col] + confusionMatrix.getConfusionInsertionValue(cInput);

                    a += confusionMatrix.getConfusionReplacementValue(cWord, cInput);

                    ed = 10 + Math.min(Math.min(a, b), c);
                }

                H[row][col] = ed;
            }
        }

        return H[m][n] / 10.0;
    }

}
