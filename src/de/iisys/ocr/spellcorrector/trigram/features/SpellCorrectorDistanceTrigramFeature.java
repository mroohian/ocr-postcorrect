package de.iisys.ocr.spellcorrector.trigram.features;

import de.iisys.ocr.spellcorrector.trigram.lattice.SpellCorrectorTrigramLatticeNode;
import de.iisys.ocr.transducer.features.UnaryFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;

/**
 * SpellCorrectorDistanceTrigramFeature
 * de.iisys.ocr.spellcorrector.trigram.features
 * Created by reza on 15.10.14.
 */
public class SpellCorrectorDistanceTrigramFeature extends UnaryFeature {
    private final double mMaxDist;

    public SpellCorrectorDistanceTrigramFeature(double maxDist) {
        super();
        setName("DistanceFeature");
        mMaxDist = maxDist;
    }

    @Override
    protected double computeUnaryValue(ILatticeNode node, String input) {
        assert (node instanceof SpellCorrectorTrigramLatticeNode);
        // TODO: use a non-linear evaluation function
        @SuppressWarnings("UnnecessaryLocalVariable")
        double value = 1.0 - (((SpellCorrectorTrigramLatticeNode)node).getDist() / mMaxDist);
        /*if (value != 0) {
            System.err.println("Distance value for " + node.getNode().getWord() + " -> " + input.getWord() + ": " + value);
        }*/
        return value;
    }
}
