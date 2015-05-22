package de.iisys.levdistcorpa.trie;

import de.iisys.levdistcorpa.types.INode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TrieSearchConfusion
 * Created by reza on 09.10.14.
 */
public class TrieSearchConfusion {
    private final Trie mTrie;
    private final IConfusionMatrix mConfusionMatrix;
    private String mWord;
    private double mMaxCutoff;
    private int m;
    private int[][] H;
    private final Map<Double, List<INode>> mResultSet;

    public TrieSearchConfusion(Trie trie, IConfusionMatrix confusionMatrix) {
        mTrie = trie;
        mConfusionMatrix = confusionMatrix;
        mResultSet = new HashMap<Double, List<INode>>();
    }

    private void printMat(int depth) {
        for(int index = 0; index < m + 1; index++) {
            for (int index1 = 0; index1 < depth + 1; index1++) {
                System.out.print(H[index][index1] + "\t");
            }
            System.out.println();
        }
    }

    public Map<Double, List<INode>> fuzzySearchWord(String word, double cutoff, boolean compound, boolean verbose) {
        String prefix = "";

        mWord = word;
        mMaxCutoff = cutoff * 10.0;
        m = word.length();

        mResultSet.clear();
        for (int dist = 0; dist <= mMaxCutoff; dist++) {
            mResultSet.put(dist / 10.0, new ArrayList<INode>());
        }

        H = new int[m + 1][TrieNode.MAX_WORD_SIZE + 1]; // Extra row for labels
        H[0][0] = 0;
        for(int row = 1; row <= m; row++) {
            H[row][0] = H[row-1][0] + 10 + mConfusionMatrix.getConfusionInsertionValue(mWord.charAt(row-1));
        }
        if (verbose) printMat(5);

        TrieNode root = mTrie.getRoot();
        fuzzySearchWordImpl(root, prefix, 0, verbose);

        H = null;
        return mResultSet;
    }

    private void fuzzySearchWordImpl(TrieNode trieNode, String prefix, int column, boolean verbose) {
        if (verbose) System.out.println("Prefix: " + prefix);

        if (column == TrieNode.MAX_WORD_SIZE + 1) {
            if (verbose) System.err.println("Backing off: maximum depth is reached.");
            return;
        }

        if (column > 0) {
            char cInput = prefix.charAt(column - 1);
            H[0][column] = H[0][column-1] + 10 + mConfusionMatrix.getConfusionInsertionValue(cInput);

            // Calculate edit distances and cutted value
            int cutted = Integer.MAX_VALUE;
            for (int row = 1; row <= m; row++)  {
                char cWord = mWord.charAt(row-1);
                int a = H[row - 1][column - 1];
                int ed;

                if (cInput == cWord) {
                    ed = a;
                } else {
                    int b = H[row][column-1] + mConfusionMatrix.getConfusionRemoveValue(cWord),
                            c = H[row-1][column] + mConfusionMatrix.getConfusionInsertionValue(cWord);

                    a += mConfusionMatrix.getConfusionReplacementValue(cInput, cWord);

                    ed = 10 + Math.min(Math.min(a, b), c);
                }

                H[row][column] = ed;

                int newCutted = Math.min(cutted, ed);
                if (cutted != newCutted) {
                    cutted = newCutted;
                }
            }
            if (verbose) printMat(column);

            // Check if least possible distance is less than maximum
            if (cutted > mMaxCutoff) {
                if (verbose) System.out.println("Backing off: max cutoff value exceeded.");
                return;
            }

            // Check if it's a match
            if (trieNode.isWordMarker()) {
                int dist = H[m][column];

                if (dist <= mMaxCutoff) {
                    double distValue = dist / 10.0;
                    mResultSet.get(distValue).add(trieNode.getNode());
                    if (verbose) System.out.println("Matched: " + prefix + "\tdistance:" + distValue);
                }
            }
        }

        // Compute children
        List<TrieNode> children = trieNode.getChildren();

        for(TrieNode child : children) {
            String newPrefix = prefix + child.getContent();
            fuzzySearchWordImpl(child, newPrefix, column + 1, verbose);
        }
    }
}