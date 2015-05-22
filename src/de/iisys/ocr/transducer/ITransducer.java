package de.iisys.ocr.transducer;

import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.types.DoubleVector;

import java.util.List;

/**
 * ITransducer
 * de.iisys.ocr.transducer
 * Created by reza on 28.07.14.
 */
public interface ITransducer {
    ILatticeNode buildLatticeNode(INode node);

    int getFeatureCount();
    List<IFeature> getFeatures();

    DoubleVector getParamsVector();
}
