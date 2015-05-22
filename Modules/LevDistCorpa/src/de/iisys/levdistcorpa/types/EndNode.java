package de.iisys.levdistcorpa.types;

/**
 * EndNode
 * Created by reza on 11.01.15.
 */
public class EndNode implements INode {
    public final static String ID = "⊤";
    public final static int INT_INDEX = Integer.MAX_VALUE;
    public final static short SHORT_INDEX = Short.MAX_VALUE;

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
