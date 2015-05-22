package de.iisys.ocr.transducer.bigram.lattice;

import de.iisys.ocr.transducer.lattice.ILatticeNode;

/**
 * IBigramLatticeNode
 * de.iisys.ocr.transducer.bigram.lattice
 * Created by reza on 15.10.14.
 */
public interface IBigramLatticeNode extends ILatticeNode {
    // forward-backward
    public double getAlpha();
    public void setAlpha(double alpha);
    public double getBeta();
    public void setBeta(double logBeta);

    // viterbi
    public double getDelta(); // log(alpha)
    public void setDelta(double logDelta);
    IBigramLatticeNode getPrevPsiNode();
    public void setPrevPsiNode(IBigramLatticeNode prevPsiNode);
}
