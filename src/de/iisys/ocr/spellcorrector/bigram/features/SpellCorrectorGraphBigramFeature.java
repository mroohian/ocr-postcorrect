package de.iisys.ocr.spellcorrector.bigram.features;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.graph.GraphDB;
import de.iisys.ocr.transducer.features.PairwiseFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;

/**
 * SpellCorrectorGraphBigramFeature
 * de.iisys.ocr.spellcorrector.bigram.features
 * Created by reza on 04.08.14.
 */
public class SpellCorrectorGraphBigramFeature extends PairwiseFeature {
    private final GraphDB mGraphDB;
    // TODO: remove from here if possible
    private TokenCorpa corpa;

    public SpellCorrectorGraphBigramFeature(GraphDB graphDB, TokenCorpa corpa) {
        this.corpa = corpa;
        setName("GraphBigramFeature");

        mGraphDB = graphDB;
    }

    @Override
    protected double computePairwiseValue(ILatticeNode prevNode, ILatticeNode node) {
        OIdentifiable rid1 = mGraphDB.getWordRID(corpa.getWordById(prevNode.getNode().getWordId()));
        if (rid1 == null) return 0;
        OIdentifiable rid2 = mGraphDB.getWordRID(corpa.getWordById(node.getNode().getWordId()));
        if (rid2 == null) return 0;
        OIdentifiable e = mGraphDB.getFollowedByEdgeRID(rid2, rid1);
        if (e == null) return 0;

        //System.err.println(prevNode.getNode().getWord() + ", " + node.getNode().getWord());
        return 1;
    }
}
