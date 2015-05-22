package de.iisys.ocr.possequence.feature;

import de.iisys.ocr.possequence.feature.core.IFeature;
import de.iisys.ocr.types.POSTrigramInfo;
import de.iisys.ocr.types.SparseVector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

/**
* Created by afsoun on 23.01.15.
*/
public class POSTrigramFeature implements IFeature {
    private final Map<Long, POSTrigramInfo> posTrigrams;
    private final long totalPOSTrigrams;
    private final double normalizer;

    public POSTrigramFeature(Map<Long, POSTrigramInfo> posTrigrams, long totalPOSTrigrams) {
        this.posTrigrams = posTrigrams;
        this.totalPOSTrigrams = totalPOSTrigrams;
        this.normalizer = -Math.log(totalPOSTrigrams);
    }
    public static POSTrigramFeature loadFeature(String posTrigramsFile) throws IOException, ClassNotFoundException  {
        // Read Trigram info from ptg file
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(posTrigramsFile));
        long totalPOSTrigrams = ois.readLong();
        Map<Long, POSTrigramInfo> posTrigrams = (Map<Long, POSTrigramInfo>) ois.readObject();
        ois.close();
        return new POSTrigramFeature(posTrigrams, totalPOSTrigrams);
    }

    public double getLogValue(short prev2, short prev1, short current) {
        long trigramId = POSTrigramInfo.generateID(prev2, prev1, current);
        POSTrigramInfo trigramInfo = posTrigrams.get(trigramId);
        if (trigramInfo != null) {
            return Math.log((double) trigramInfo.getFreq()) - normalizer; // Math.log((double)trigramInfo.getFreq() / totalPOSTrigrams);
        } else {
            return normalizer; //Math.log(1/totalPOSTrigrams);
        }
    }

    @Override
    public double getLogValue(short prev2, short prev1, short current, SparseVector[] input, int j) {
        return getLogValue(prev2, prev1, current);
    }
}
