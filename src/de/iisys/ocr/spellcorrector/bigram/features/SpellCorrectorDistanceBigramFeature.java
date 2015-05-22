package de.iisys.ocr.spellcorrector.bigram.features;

import de.iisys.ocr.spellcorrector.bigram.lattice.SpellCorrectorBigramLatticeNode;
import de.iisys.ocr.transducer.features.UnaryFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;

/**
 * SpellCorrectorDistanceBigramFeature
 * de.iisys.ocr.spellcorrector.bigram.features
 * Created by reza on 04.08.14.
 */
public class SpellCorrectorDistanceBigramFeature extends UnaryFeature {
    private final double mMaxDist;

    public SpellCorrectorDistanceBigramFeature(double maxDist) {
        super();
        setName("DistanceFeature");
        mMaxDist = maxDist;
    }

    @Override
    protected double computeUnaryValue(ILatticeNode node, String input) {
        assert (node instanceof SpellCorrectorBigramLatticeNode);
        // TODO: use a non-linear evaluation function
        @SuppressWarnings("UnnecessaryLocalVariable")
        double value = 1.0 - (((SpellCorrectorBigramLatticeNode)node).getDist() / mMaxDist);
        /*if (value != 0) {
            System.err.println("Distance value for " + node.getNode().getWord() + " -> " + input.getWord() + ": " + value);
        }*/
        return value;
    }
}
