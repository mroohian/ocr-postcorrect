package de.iisys.levdistcorpa.trie;

/**
 * ConfusionMatrix
 * de.iisys.levdistcorpa.trie
 * Created by reza on 09.10.14.
 */
public class ConfusionMatrix implements IConfusionMatrix {
    // TOOD: make the actual confusion matrices

    public int getConfusionReplacementValue(char a, char b) {
        int penalty = 2;
        if (Character.isLowerCase(a) != Character.isLowerCase(b)) {
            penalty ++;
        }
        return penalty;
    }

    public int getConfusionInsertionValue(char a) {
        return 1;
    }

    public int getConfusionRemoveValue(char a) {
        return 1;
    }
}