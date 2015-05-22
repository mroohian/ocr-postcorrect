package de.iisys.ocr.transducer.trigram;

import de.iisys.ocr.transducer.ITransducer;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.trigram.lattice.ITrigramLatticeNode;
import de.iisys.ocr.transducer.trigram.lattice.ITrigramLatticeNodeList;

/**
 * ITrigramTransducer
 * de.iisys.ocr.transducer.trigram
 * Created by reza on 15.10.14.
 */
public interface ITrigramTransducer extends ITransducer {
    ITrigramLatticeNodeList buildCandidateList(int sequencePosition, int sequenceLength, String input, ITrigramLatticeNodeList prevList);

    double getFeatureValue(int i, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node, String[] inputs, Object[] extras, int j);
    double getLogCliqueValue(int j, Object[] extras, String[] inputs, ITrigramLatticeNode prevNode2, ITrigramLatticeNode prevNode1, ITrigramLatticeNode node);
}
