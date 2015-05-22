package de.iisys.levdistcorpa.trie;

import de.iisys.levdistcorpa.alphabet.WordAlphabet;
import de.iisys.levdistcorpa.types.INode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TrieSearch
 * de.iisys.levdistcorpa.trie
 * Created by reza on 28.07.14.
 */
public class TrieSearch {
    public static final int RESULT_FOUND_WORD = 0;
    public static final int RESULT_FOUND_PREFIX = 1;
    public static final int RESULT_NOT_FOUND = 2;

    private final Trie mTrie;
    private WordAlphabet wordAlphabet;
    private String mWord;
    private int mCutoff;
    private int m;
    private int[][] H;
    private final Map<Double, List<INode>> mResultSet;

    public TrieSearch(Trie trie, WordAlphabet wordAlphabet) {
        mTrie = trie;
        this.wordAlphabet = wordAlphabet;
        mResultSet = new HashMap<Double, List<INode>>();
    }

    private void printMat(int depth) {
        for(int index = 0; index < m + 1; index++) {
            for (int index1 = 0; index1 < depth + 1; index1++) {
                System.err.print(H[index][index1]);
            }
            System.err.println();
        }
    }

    public int searchWord(String word) {
        // TODO: not implemented
        TrieNode current = mTrie.getRoot();

        //noinspection LoopStatementThatDoesntLoop
        while (null != current) {
            for (int i = 0; i < word.length(); i++) {
                TrieNode tmp = current.findChild(word.charAt(i));
                if (tmp == null) {
                    return RESULT_NOT_FOUND;
                }
                current = tmp;
            }

            if (current.isWordMarker())
                return RESULT_FOUND_WORD;
            else
                return RESULT_FOUND_PREFIX;
        }

        return RESULT_NOT_FOUND;
    }


    public int searchCompoundWord(String word) {
        TrieNode current = mTrie.getRoot();

        //noinspection LoopStatementThatDoesntLoop
        while (null != current) {
            for (int i = 0; i < word.length(); i++) {
                TrieNode tmp = current.findChild(word.charAt(i));
                if (tmp == null) {
                    if (current.isWordMarker()) {
                        tmp = mTrie.getRoot().findChild(word.charAt(i));
                    }

                    if (tmp == null) {
                        return RESULT_NOT_FOUND;
                    }
                }
                current = tmp;
            }

            if (current.isWordMarker())
                return RESULT_FOUND_WORD;
            else
                return RESULT_FOUND_PREFIX;
        }

        return RESULT_NOT_FOUND;
    }

    private void fuzzySearchWordImpl(TrieNode trieNode, String prefix, int depth, boolean compound, boolean isCompound, boolean verbose) {
        if (depth == TrieNode.MAX_WORD_SIZE) {
            if (verbose) System.err.println("Backing off: maximum depth is reached.");
            return;
        }

        if (depth > 0) {
            int l = Math.max(1, depth - mCutoff),
                    u = Math.min(m, depth + mCutoff);

            if (u < l) {
                if (verbose) System.err.println("Backing off: no possible value");
                return;
            }


            // Calculate edit distances and cutted value
            int cutted = Integer.MAX_VALUE; // Invalid cutted value
            //int cuttedIndex = Integer.MAX_VALUE;
            // TODO: make better use of l and u value
            for (int row = 1; row <= m; row++) {
                int a = H[row - 1][depth - 1], ed;

                if (verbose) System.err.println("Comparing " + mWord.charAt(row - 1) + " and " + prefix.charAt(depth - 1));
                if (mWord.charAt(row-1) == prefix.charAt(depth-1)) {
                    ed = a;
                } else {
                    int b = H[row][depth-1],
                            c = H[row-1][depth];

                    ed = 1 + Math.min(Math.min(a, b), c);
                }

                if (row >= l && row <= u) {
                    int newCutted = Math.min(cutted, ed);
                    if (cutted != newCutted) {
                        cutted = newCutted;
                        //cuttedIndex = row;
                    }
                }

                H[row][depth] = ed;
            }
            if (verbose) printMat(depth);
            if (verbose) System.err.println("cutted value: " + cutted);

            if (cutted > mCutoff) {
                if (verbose) System.err.println("Backing off: cutoff value exceeded.");
                return;
            }

            // Check if it's a match
            if (trieNode.isWordMarker()) {
                // at this point we require m - cuttedIndex insertations
                //int dist = cutted + (m - cuttedIndex);
                double dist = H[m][depth];

                if (dist <= mCutoff) {
                    if (isCompound) {
                        int compoundWordId = wordAlphabet.lookup(prefix, true);
                        mResultSet.get(dist).add(trieNode.getNode().makeNew(compoundWordId));
                    } else {
                        mResultSet.get(dist).add(trieNode.getNode());
                    }
                    if (verbose) System.err.println("Matched: " + prefix + "\tdistance:" + dist);
                }
            }
        }

        // Compute children
        List<TrieNode> children = trieNode.getChildren();

        if (children.size() == 0 && compound && trieNode.isWordMarker()) {
            children = mTrie.getRoot().getChildren();
            isCompound = true;
        }

        for(TrieNode child : children) {
            String newPrefix = prefix + child.getContent();
            fuzzySearchWordImpl(child, newPrefix, depth + 1, compound, isCompound, verbose);
        }
    }

    public Map<Double, List<INode>> fuzzySearchWord(String word, double cutoff, boolean compound, boolean verbose) {
        String prefix = "";

        mWord = word;
        mCutoff = (int)cutoff;
        m = word.length();

        mResultSet.clear();
        for (double dist = 0; dist <= cutoff; dist++) {
            mResultSet.put(dist, new ArrayList<INode>());
        }

        H = new int[m + 1][TrieNode.MAX_WORD_SIZE]; // Extra row for labels
        for(int index = 0; index < m + 1; ++index) {
            // Fill table
            if (index == 0) {
                for (int index1 = 0; index1 < TrieNode.MAX_WORD_SIZE; index1++)
                    H[index][index1] = index1;
            } else {
                H[index][0] = index;
            }
        }
        if (verbose) printMat(5);

        TrieNode root = mTrie.getRoot();
        fuzzySearchWordImpl(root, prefix, 0, compound, false, verbose);

        H = null;
        return mResultSet;
    }
}