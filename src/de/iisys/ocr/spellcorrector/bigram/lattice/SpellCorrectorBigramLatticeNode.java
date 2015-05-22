package de.iisys.ocr.spellcorrector.bigram.lattice;

import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.bigram.lattice.BigramLatticeNode;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.types.Token;

/**
 * SpellCorrectorBigramLatticeNode
 * de.iisys.ocr.spellcorrector.bigram.lattice
 * Created by reza on 28.07.14.
 */
public class SpellCorrectorBigramLatticeNode extends BigramLatticeNode {
    private final int mDist;

    public SpellCorrectorBigramLatticeNode(Token token, int dist) {
        this(token, dist, false);
    }

    public SpellCorrectorBigramLatticeNode(Token token, int dist, boolean isBoundary) {
        super(token, isBoundary);
        mDist = dist;
    }

    public int getDist() {
        return mDist;
    }

    private String getPsiString(){
        ILatticeNode prevPsiNode = getPrevPsiNode();
        if (prevPsiNode != null && prevPsiNode instanceof SpellCorrectorBigramLatticeNode) {
            return ((SpellCorrectorBigramLatticeNode)prevPsiNode).getPsiString() + " " + getPsiValue();
        }

        return getPsiValue();
    }

    public String getPsiValue() {
        return "N" + mToken.getWordId();
    }

    @Override
    public String toString() {
        return String.format("α:%.2f β:%.2f δ:%.2f Ψ:%s", getAlpha(), getBeta(), getDelta(), getPsiString());
    }

    @Override
    public String getDetails() {
        INode node = getNode();
        return String.format("N%d(D:%d R:%d α:%.2f β:%.2f δ:%.2f)", node.getWordId(), getDist(), node.getRank(), getAlpha(), getBeta(), getDelta());
    }
}
