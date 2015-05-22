package de.iisys.ocr.transducer.bigram.lattice;

import java.util.ListIterator;

/**
 * IBigramLattice
 * de.iisys.ocr.transducer.bigram.lattice
 * Created by reza on 15.10.14.
 */
public interface IBigramLattice extends Iterable<IBigramLatticeNodeList> {
    ListIterator<IBigramLatticeNodeList> forwardIterator();
    ListIterator<IBigramLatticeNodeList> reverseIterator();
}
