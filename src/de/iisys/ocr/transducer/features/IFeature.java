package de.iisys.ocr.transducer.features;

import de.iisys.ocr.transducer.lattice.ILatticeNode;

/**
 * IFeature
 * de.iisys.ocr.transducer.features
 * Created by reza on 04.08.14.
 */
public interface IFeature {
    @SuppressWarnings("UnusedDeclaration")
    public String getName();
    public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node);
}
