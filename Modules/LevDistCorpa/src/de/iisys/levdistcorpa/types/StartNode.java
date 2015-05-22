package de.iisys.levdistcorpa.types;

/**
 * StartNode
 * Created by reza on 11.01.15.
 */
public class StartNode implements INode {
    public final static String ID = "⊥";
    public final static int INT_INDEX = Integer.MIN_VALUE;
    public final static short SHORT_INDEX = Short.MIN_VALUE;

    @Override
    public int getWordId() {
        return INT_INDEX;
    }

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public INode makeNew(int wordId) {
        return null;
    }
}
