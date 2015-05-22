package de.iisys.ocr.transducer.bigram;

import de.iisys.ocr.transducer.ITransducer;
import de.iisys.ocr.transducer.bigram.lattice.IBigramLatticeNode;
import de.iisys.ocr.transducer.bigram.lattice.IBigramLatticeNodeList;
import de.iisys.ocr.transducer.lattice.ILatticeNode;

/**
 * IBigramTransducer
 * de.iisys.ocr.transducer.bigram
 * Created by reza on 15.10.14.
 */
public interface IBigramTransducer extends ITransducer {
    IBigramLatticeNodeList buildCandidateList(int sequencePosition, int sequenceLength, String input);

    double getFeatureValue(int i, ILatticeNode prevNode, ILatticeNode node, String[] inputs, Object[] extras, int j);
    double getLogCliqueValue(int j, Object[] extras, String[] inputs, IBigramLatticeNode prevNode, IBigramLatticeNode node);
}
