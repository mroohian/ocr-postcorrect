package de.iisys.ocr.transducer.trigram.lattice;

import java.util.ListIterator;

/**
 * ITrigramLattice
 * de.iisys.ocr.transducer.trigram.lattice
 * Created by reza on 15.10.14.
 */
public interface ITrigramLattice extends Iterable<ITrigramLatticeNodeList> {
    ListIterator<ITrigramLatticeNodeList> forwardIterator();
    ListIterator<ITrigramLatticeNodeList> reverseIterator();
}
