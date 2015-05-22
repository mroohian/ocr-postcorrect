package de.iisys.ocr.transducer.features;

import de.iisys.ocr.transducer.lattice.ILatticeNode;

/**
 * UnaryFeature
 * de.iisys.ocr.transducer.features
 * Created by reza on 04.08.14.
 */
public abstract class UnaryFeature extends Feature {
    @Override
    public final double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
        if (j > 0 && !node.isBoundaryNode()) return computeUnaryValue(node, inputs[j]);
        //if (prevNode != null) return computeUnaryValue(prevNode, inputs[j]);
        return 0.0;
    }

    protected abstract double computeUnaryValue(ILatticeNode node, String input);
}
