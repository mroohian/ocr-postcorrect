package de.iisys.ocr.transducer.lattice;

import de.iisys.levdistcorpa.types.INode;

/**
 * ILatticeNode
 * de.iisys.ocr.transducer.lattice
 * Created by reza on 04.08.14.
 */
public interface ILatticeNode {
    public INode getNode();
    public String getDetails();

    boolean isBoundaryNode();
}
