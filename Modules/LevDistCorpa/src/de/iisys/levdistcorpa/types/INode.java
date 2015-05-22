package de.iisys.levdistcorpa.types;

/**
 * INode
 * de.iisys.levdistcorpa.trie
 * Created by reza on 20.08.14.
 */
public interface INode {
    public int getWordId();
    public int getRank();

    public INode makeNew(int wordId);
}
