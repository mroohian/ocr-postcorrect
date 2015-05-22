package de.iisys.levdistcorpa.trie;

/**
 * IConfusionMatrix
 * de.iisys.levdistcorpa.trie
 * Created by reza on 09.10.14.
 */
public interface IConfusionMatrix {
    int getConfusionReplacementValue(char a, char b);
    int getConfusionInsertionValue(char a);
    int getConfusionRemoveValue(char a);
}