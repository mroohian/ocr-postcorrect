package de.iisys.ocr.possequence.feature;

import de.iisys.ocr.possequence.feature.core.IFeature;
import de.iisys.ocr.types.SparseVector;

/**
 * NextHasPropertyFeature
 * Created by reza on 25.01.15.
 */
public class NextHasPropertyFeature  implements IFeature {
    private final int propertyIndex;
    private final short label;

    public NextHasPropertyFeature(int propertyIndex, short label) {
        this.propertyIndex = propertyIndex;
        this.label = label;
    }

    @Override
    public double getLogValue(short prev2, short prev1, short current, SparseVector[] input, int j) {
        return input[j].get(propertyIndex) == 1 && prev1 == label ? 1 : 0;
    }
}
