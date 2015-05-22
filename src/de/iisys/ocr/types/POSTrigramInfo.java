package de.iisys.ocr.types;

import java.io.Serializable;

/**
 * POSTrigramInfo
 * Created by reza on 10.01.15.
 */
public class POSTrigramInfo implements Comparable<POSTrigramInfo>, Serializable {
    private final long id;
    private final short[] posIDs;
    private long freq;

    public POSTrigramInfo(short[] posIDs) {
        this.posIDs = posIDs;
        id = generateID(posIDs);
        freq = 1;
    }

    public long getId() {
        return id;
    }

    public short[] getPosIDs() {
        return posIDs;
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

    public static long generateID(short[] posIDs) {
        return generateID(posIDs[0], posIDs[1], posIDs[2]);
    }

    public static long generateID(short posID1, short posID2, short posID3) {
        return ((long)posID1) << 32 | ((long)posID2) << 16 | ((long)posID3);
    }

    @Override
    public String toString() {
        return posIDs[0] + "_" + posIDs[1] + "_" + posIDs[2] + ", " + freq;
    }

    @Override
    public int compareTo(POSTrigramInfo o) {
        return Long.compare(o.freq, this.freq);
    }
}
