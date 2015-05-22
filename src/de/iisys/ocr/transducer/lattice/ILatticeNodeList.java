package de.iisys.ocr.transducer.lattice;

/**
 * ILatticeNodeList
 * de.iisys.ocr.transducer.lattice
 * Created by reza on 09.10.14.
 */
public interface ILatticeNodeList {
    int getSequencePosition();
    int size();
    ILatticeNode get(int a);
}
