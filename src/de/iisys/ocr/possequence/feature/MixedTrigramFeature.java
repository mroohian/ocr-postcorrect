package de.iisys.ocr.possequence.feature;

import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.possequence.feature.core.IFeature;
import de.iisys.ocr.types.MixedTrigramInfo;
import de.iisys.ocr.types.SparseVector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * MixedTrigramFeature
 * Created by reza on 23.01.15.
 */
public class MixedTrigramFeature implements IFeature {
    private final Map<Long, MixedTrigramInfo> mixedTrigrams;
    private final long totalMixedTrigrams;
    private final double normalizer;
    private TokenCorpa corpa;
    private final int propertyIndex;

    public MixedTrigramFeature(int propertyIndex, Map<Long, MixedTrigramInfo> mixedTrigrams, long totalMixedTrigrams, TokenCorpa corpa) {
        this.propertyIndex = propertyIndex;
        this.mixedTrigrams = mixedTrigrams;
        this.totalMixedTrigrams = totalMixedTrigrams;
        this.corpa = corpa;
        this.normalizer = -Math.log(totalMixedTrigrams);
    }

    @Override
    public double getLogValue(short prev2, short prev1, short current, SparseVector[] input, int j) {
        SparseVector wordProperties = input[j];
        int wordId = wordProperties.get(propertyIndex);
        if (wordId == -1) return normalizer;

        long trigramId = MixedTrigramInfo.generateID(prev2, wordId, current);
        MixedTrigramInfo trigramInfo = mixedTrigrams.get(trigramId);
        if (trigramInfo == null) return normalizer;

        return Math.log(trigramInfo.getFreq()) - normalizer;
    }

    public static MixedTrigramFeature loadFeature(String posTrigramsFile, TokenCorpa corpa, int propertyIndex) throws IOException, ClassNotFoundException  {
        // Read Trigram info from mtg file
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(posTrigramsFile));
        long totalMixedTrigrams = ois.readLong();
        Map<Long, MixedTrigramInfo> mixedTrigrams = (Map<Long, MixedTrigramInfo>) ois.readObject();
        ois.close();

        return new MixedTrigramFeature(propertyIndex, mixedTrigrams, totalMixedTrigrams, corpa);
    }
}
