package de.iisys.levdistcorpa.corpa;

import de.iisys.levdistcorpa.alphabet.POSAlphabet;
import de.iisys.levdistcorpa.alphabet.WordAlphabet;
import de.iisys.levdistcorpa.trie.*;
import de.iisys.levdistcorpa.types.INode;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

/**
 * Corpa
 * de.iisys.levdistcorpa.corpa
 * Created by reza on 28.07.14.
 */
public abstract class Corpa implements ICorpa {
    private final Trie mTrie;
    private final TrieSearch mTrieSearch;
    private final TrieSearchConfusion mTrieSearchConfusion;
    private final List<INode> mNodes;
    private final POSAlphabet posAlphabet;
    private final WordAlphabet wordAlphabet;

    public Corpa() {
        mTrie = new Trie();

        mNodes = new LinkedList<INode>();
        posAlphabet = new POSAlphabet();
        wordAlphabet = new WordAlphabet();

        mTrieSearch = new TrieSearch(mTrie, wordAlphabet);
        mTrieSearchConfusion = new TrieSearchConfusion(mTrie, new ConfusionMatrix());
    }

    public Corpa(WordAlphabet wordAlphabet, POSAlphabet posAlphabet) {
        mTrie = new Trie();

        mNodes = new LinkedList<INode>();
        this.posAlphabet = posAlphabet;
        this.wordAlphabet = wordAlphabet;

        mTrieSearch = new TrieSearch(mTrie, wordAlphabet);
        mTrieSearchConfusion = new TrieSearchConfusion(mTrie, new ConfusionMatrix());
    }

    public int addWord(String word) {
        return wordAlphabet.lookup(word, true);
    }

    public int getWordId(String word) throws NullPointerException {
        return wordAlphabet.lookup(word, false);
    }

    public String getWordById(int wordId) throws NullPointerException {
        return wordAlphabet.lookupValue(wordId);
    }

    public short addPOS(String pos) {
        return posAlphabet.lookup(pos, true);
    }

    public short getPOSID(String pos) throws NullPointerException {
        return posAlphabet.lookup(pos, false);
    }

    public String getPOSById(short posID) throws NullPointerException {
        return posAlphabet.lookupValue(posID);
    }

    protected void addEntry(INode node, int flag) {
        mNodes.add(node);
        mTrie.addWord(wordAlphabet.lookupValue(node.getWordId()), node, flag);
    }

    public int size() {
        return mNodes.size();
    }

    public List<INode> getAllNodes() {
        return mNodes;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean hasWord(String word) {
        return mTrieSearch.searchWord(word) == TrieSearch.RESULT_FOUND_WORD;
    }

    public boolean hasCompoundWord(String word) {
        return mTrieSearch.searchCompoundWord(word) == TrieSearch.RESULT_FOUND_WORD;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean hasWordOrPrefix(String word) {
        return mTrieSearch.searchWord(word) != TrieSearch.RESULT_NOT_FOUND;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<Double,List<INode>> findSimilarWords(String word, double cutoff) {
        return mTrieSearchConfusion.fuzzySearchWord(word, cutoff, false, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<Double, List<INode>> findSimilarWordsNoConfusionMatrix(String word,  double cutoff) {
        return mTrieSearch.fuzzySearchWord(word, cutoff, false, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<Double,List<INode>> findSimilarCompoundWordsNoConfusionMatrix(String compoundWord, double cutoff) {
        return mTrieSearch.fuzzySearchWord(compoundWord, cutoff, true, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<Double,List<INode>> findSimilarWordsDebug(String word, double cutoff) {
        return mTrieSearchConfusion.fuzzySearchWord(word, cutoff, false, true);
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<Double, List<INode>> findSimilarWordsNoConfusionMatrixDebug(String word,  double cutoff) {
        return mTrieSearch.fuzzySearchWord(word, cutoff, false, true);
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<Double,List<INode>> findSimilarCompoundWordsNoConfusionMatrixDebug(String compoundWord, double cutoff) {
        return mTrieSearch.fuzzySearchWord(compoundWord, cutoff, true, true);
    }

    protected WordAlphabet getWordAlphabet() {
        return wordAlphabet;
    }

    protected POSAlphabet getPosAlphabet() {
        return posAlphabet;
    }
}