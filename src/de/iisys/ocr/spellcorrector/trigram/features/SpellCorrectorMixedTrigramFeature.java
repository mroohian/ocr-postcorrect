package de.iisys.ocr.spellcorrector.trigram.features;

import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.types.MixedTrigramInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * SpellCorrectorMixedTrigramFeature
 * Created by reza on 04.02.15.
 */
public class SpellCorrectorMixedTrigramFeature implements IFeature {
    private final TokenCorpa corpa;
    private final Map<Long, MixedTrigramInfo> mixedTrigrams;
    private final long totalMixedTrigrams;
    private final double normalizer;

    private SpellCorrectorMixedTrigramFeature(Map<Long, MixedTrigramInfo> mixedTrigrams, long totalMixedTrigrams, TokenCorpa corpa) {
        this.mixedTrigrams = mixedTrigrams;
        this.totalMixedTrigrams = totalMixedTrigrams;
        this.corpa = corpa;
        this.normalizer = 1.0 / totalMixedTrigrams;
    }

    @Override
    public String getName() {
        return "SpellCorrectorMixedTrigramFeature";
    }

    @Override
    public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
        Short[] posIDs = (Short[])extras;

        int wordId = prevNode1.getNode().getWordId();
        if (wordId == -1) return normalizer;

        long trigramId = MixedTrigramInfo.generateID(posIDs[j], wordId, posIDs[j+2]);
        MixedTrigramInfo trigramInfo = mixedTrigrams.get(trigramId);
        if (trigramInfo == null) return normalizer;

        //return Math.log(trigramInfo.getFreq()) - normalizer;
        return trigramInfo.getFreq() * normalizer;
    }
    public static SpellCorrectorMixedTrigramFeature loadFeature(String posTrigramsFile, TokenCorpa corpa) throws IOException, ClassNotFoundException  {
        // Read Trigram info from mtg file
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(posTrigramsFile));
        long totalMixedTrigrams = ois.readLong();
        Map<Long, MixedTrigramInfo> mixedTrigrams = (Map<Long, MixedTrigramInfo>) ois.readObject();
        ois.close();

        return new SpellCorrectorMixedTrigramFeature(mixedTrigrams, totalMixedTrigrams, corpa);
    }
}
