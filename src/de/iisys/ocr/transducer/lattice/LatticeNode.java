package de.iisys.ocr.transducer.lattice;

import de.iisys.levdistcorpa.types.INode;

/**
 * LatticeNode
 * de.iisys.ocr.transducer.lattice
 * Created by reza on 28.07.14.
 */
public abstract class LatticeNode implements ILatticeNode {
    protected final INode mToken;
    private final boolean mIsBoundary;

    /*public LatticeNode(INode token) {
        mToken = token;
        mIsBoundary = false;
        //mIsBoundary = (token == Token.START_NODE || token == Token.STOP_NODE);
    }*/

    public LatticeNode(INode token, boolean isBoundary) {
        mToken = token;
        mIsBoundary = isBoundary;
    }

    public INode getNode() {
        return mToken;
    }

    @Override
    public boolean isBoundaryNode() {
        return mIsBoundary;
    }
}
