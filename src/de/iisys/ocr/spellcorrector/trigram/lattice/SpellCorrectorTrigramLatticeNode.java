package de.iisys.ocr.spellcorrector.trigram.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.spellcorrector.bigram.lattice.SpellCorrectorBigramLatticeNode;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.trigram.lattice.ITrigramLatticeNode;
import de.iisys.ocr.transducer.trigram.lattice.ITrigramLatticeNodeList;
import de.iisys.ocr.transducer.trigram.lattice.TrigramLatticeNode;
import de.iisys.ocr.types.Token;

/**
 * SpellCorrectorTrigramLatticeNode
 * de.iisys.ocr.spellcorrector.trigram.lattice
 * Created by reza on 15.10.14.
 */
public class SpellCorrectorTrigramLatticeNode extends TrigramLatticeNode {
    private final int mDist;

    public SpellCorrectorTrigramLatticeNode(Token token, ITrigramLatticeNodeList prevList, int dist) {
        this(token, prevList, dist, false);
    }

    public SpellCorrectorTrigramLatticeNode(Token token, ITrigramLatticeNodeList prevList, int dist, boolean isBoundary) {
        super(token, prevList, isBoundary);
        mDist = dist;
    }

    public int getDist() {
        return mDist;
    }

    private String getPsiString(ITrigramLatticeNode prevNode){
        ILatticeNode prevPsiNode = getPrevPsiNode(prevNode);
        if (prevPsiNode != null && prevPsiNode instanceof SpellCorrectorBigramLatticeNode) {
            return ((SpellCorrectorTrigramLatticeNode)prevPsiNode).getPsiString(prevNode) + " " + getPsiValue();
        }

        return getPsiValue();
    }

    public String getPsiValue() {
        return "N" + mToken.getWordId();
    }

    @Override
    public String toString() {
        return getDetails();
    }

    @Override
    public String getDetails() {
        INode node = getNode();
        return String.format("N%d(D:%d R:%d)", node.getWordId(), getDist(), node.getRank());
    }
}
