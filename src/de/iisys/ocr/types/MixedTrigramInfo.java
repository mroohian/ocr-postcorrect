package de.iisys.ocr.types;

import java.io.Serializable;

/**
 * MixedTrigramInfo
 * Created by reza on 11.01.15.
 */
public class MixedTrigramInfo implements Comparable<MixedTrigramInfo>, Serializable {
    private final long id;
    private final short posID1, posID2;
    private final int wordID;
    private long freq;

    public MixedTrigramInfo(short posID1, int wordID, short posID2) {
        this.posID1 = posID1;
        id = generateID(posID1, wordID, posID2);
        this.wordID = wordID;
        this.posID2 = posID2;
        freq = 1;
    }

    public long getId() {
        return id;
    }

    public short getPosID1() {
        return posID1;
    }

    public int getWordID() {
        return wordID;
    }

    public short getPosID2() {
        return posID2;
    }

    public long getFreq() {
        return freq;
    }

    public void setFreq(long freq) {
        this.freq = freq;
    }

    public void increaseFreq() {
        freq++;
    }

    public static long generateID(short posID1, int wordID, short posID2) {
        return ((long)posID1) << 48 | ((long)wordID) << 16 | ((long)posID2);
    }

    /*public String[] getPOSTrigram() {
        return new String[] {
                POSAlphabet.getInstance().lookupValue(posID1),
                WordAlphabet.getInstance().lookupValue(wordID),
                POSAlphabet.getInstance().lookupValue(posID2)
        };
    }

    public String getMixedTrigramString() {
        String[] mixedTrigram = getPOSTrigram();
        return mixedTrigram[0] + "_" + mixedTrigram[1] + "_" + mixedTrigram[2];
    }*/

    @Override
    public String toString() {
        return posID1 + "_" + wordID + "-" + posID2 + ", " + freq;
    }

    @Override
    public int compareTo(MixedTrigramInfo o) {
        return Long.compare(o.freq, this.freq);
    }
}