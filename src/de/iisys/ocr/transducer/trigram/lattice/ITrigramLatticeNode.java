package de.iisys.ocr.transducer.trigram.lattice;

import de.iisys.ocr.transducer.lattice.ILatticeNode;

/**
 * ITrigramLatticeNode
 * de.iisys.ocr.transducer.trigram.lattice
 * Created by reza on 15.10.14.
 */
public interface ITrigramLatticeNode extends ILatticeNode {
    // forward-backward
    public double getAlpha(ITrigramLatticeNode prevNode);
    public void setAlpha(ITrigramLatticeNode prevNode, double alpha);
    public double getBeta(ITrigramLatticeNode prevNode);
    public void setBeta(ITrigramLatticeNode prevNode, double logBeta);

    // viterbi
    public double getDelta(ITrigramLatticeNode prevNode); // log(alpha)
    public void setDelta(ITrigramLatticeNode prevNode, double logDelta);
    ITrigramLatticeNode getPrevPsiNode(ITrigramLatticeNode prevNode);
    public void setPrevPsiNode(ITrigramLatticeNode prevNode, ITrigramLatticeNode prevPsiNode);
}
