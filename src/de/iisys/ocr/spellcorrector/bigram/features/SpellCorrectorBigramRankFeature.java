package de.iisys.ocr.spellcorrector.bigram.features;

import de.iisys.ocr.spellcorrector.bigram.lattice.SpellCorrectorBigramLatticeNode;
import de.iisys.ocr.transducer.features.UnaryFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.types.Token;

/**
 * SpellCorrectorBigramRankFeature
 * de.iisys.ocr.spellcorrector.bigram.features
 * Created by reza on 04.08.14.
 */
public class SpellCorrectorBigramRankFeature extends UnaryFeature {
    private final static double MAX_RANK = 30.0;
    private final double MAX_RANK_CONST;

    public SpellCorrectorBigramRankFeature() {
        super();
        setName("RankFeature");
        MAX_RANK_CONST = 1.0 / Math.log(1 + MAX_RANK);
    }

    @Override
    protected double computeUnaryValue(ILatticeNode node, String input) {
        assert (node instanceof SpellCorrectorBigramLatticeNode);
        int rank = node.getNode().getRank();

        if (rank == Token.INVALID_RANK) return 0.0;

        assert rank >= 0;

        //double value = (MAX_RANK - rank) / MAX_RANK;

        // use a non-linear evaluation function
        // 1 - (log(1+x) / log(maxRank + 1)) where 0<x<maxRank

        @SuppressWarnings("UnnecessaryLocalVariable")
        double value = 1.0 - (Math.log(rank + 1.0) * MAX_RANK_CONST);

        //System.err.println("Rank value for " + node.getNode().getWord()  + ": " + value);
        return value;
    }
}
