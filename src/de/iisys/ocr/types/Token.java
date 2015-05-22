package de.iisys.ocr.types;

import de.iisys.levdistcorpa.types.EndNode;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.levdistcorpa.types.StartNode;

import java.io.Serializable;

/**
 * Token
 * de.iisys.ocr.types
 * Created by reza on 28.07.14.
 */
public class Token implements INode, Serializable {
    public static final INode START_NODE = new StartNode();
    public static final INode STOP_NODE = new EndNode();
    public static int INVALID_RANK = -1;
    private final int wordId;
    protected int rank;

    /* Used by serializer */
    protected Token() {
        wordId = -1;
    }

    public Token(int wordId) {
        this.wordId = wordId;
        rank = INVALID_RANK;
    }

    public Token(int wordId, int rank) {
        this.wordId = wordId;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return String.valueOf(wordId);
    }

    @Override
    public int getWordId() {
        return wordId;
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public INode makeNew(int wordId) {
        return new Token(wordId);
    }
}
