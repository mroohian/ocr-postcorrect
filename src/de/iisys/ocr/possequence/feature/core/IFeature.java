package de.iisys.ocr.possequence.feature.core;

import de.iisys.ocr.types.SparseVector;

/**
 * IFeature
 * Created by reza on 23.01.15.
 */
public interface IFeature {
    double getLogValue(short prev2, short prev1, short current, SparseVector[] input, int j);
}
