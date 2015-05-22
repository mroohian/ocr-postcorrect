package de.iisys.ocr.transducer.bigram.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.transducer.lattice.LatticeNode;

/**
 * BigramLatticeNode
 * de.iisys.ocr.transducer.bigram.lattice
 * Created by reza on 15.10.14.
 */
public abstract class BigramLatticeNode extends LatticeNode implements IBigramLatticeNode {
    // forward-backward
    private double mAlpha; // log(alpha)
    private double mBeta; // log(beta)

    // viterbi
    private double mDelta; // log(delta)
    private IBigramLatticeNode mPrevPsiNode;

    /*public BigramLatticeNode(INode token) {
        super(token, false);
    }*/

    public BigramLatticeNode(INode token, boolean isBoundaryNode) {
        super(token, isBoundaryNode);
    }

    public double getAlpha() {
        return mAlpha;
    }

    public void setAlpha(double alpha) {
        mAlpha = alpha;
    }

    public double getBeta() {
        return mBeta;
    }

    public void setBeta(double beta) {
        mBeta = beta;
    }

    public double getDelta() {
        return mDelta;
    }

    public void setDelta(double mDelta) {
        this.mDelta = mDelta;
    }

    public IBigramLatticeNode getPrevPsiNode() {
        return mPrevPsiNode;
    }

    public void setPrevPsiNode(IBigramLatticeNode prevPsiNode) {
        this.mPrevPsiNode = prevPsiNode;
    }
}
