package de.iisys.ocr.transducer.features;

import de.iisys.ocr.transducer.lattice.ILatticeNode;

/**
 * PairwiseFeature
 * de.iisys.ocr.transducer.features
 * Created by reza on 04.08.14.
 */
public abstract class PairwiseFeature extends Feature {
    @Override
    public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
        //if (prevNode == null || node == null) return 0;
        return computePairwiseValue(prevNode1, node);
    }

    protected abstract double computePairwiseValue(ILatticeNode prevNode, ILatticeNode node);
}
