package de.iisys.ocr.types;

import de.iisys.levdistcorpa.types.INode;

import java.io.Serializable;
import java.util.*;

/**
 * WordPOSInfo
 * Created by reza on 10.01.15.
 */
public class WordPOSInfo extends Token implements Comparable<WordPOSInfo>, Serializable {
    private long freq;
    private final List<Short> posTagIDs;

    /* Used by serializer */
    private WordPOSInfo() {
        super();
        posTagIDs = null;
    }

    public WordPOSInfo(int wordId) {
        super(wordId);
        freq = 1;
        posTagIDs = new ArrayList<Short>();
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public INode makeNew(int wordId) {
        return new WordPOSInfo(wordId);
    }

    public long getFreq() {
        return freq;
    }

    public void increaseFreq() {
        freq++;
    }

    public void setFreq(long freq) {
        this.freq = freq;
    }

    public List<Short> getPosTagIDs() {
        return posTagIDs;
    }

    public boolean addPosTags(short posId) {
        if (posTagIDs.contains(posId)){
            return false;
        }
        posTagIDs.add(posId);
        return true;
    }

    @Override
    public int compareTo(WordPOSInfo o) {
        return Long.compare(o.freq, this.freq);
    }

    @Override
    public String toString() {
        return super.toString() + ", " + freq;
    }

}
