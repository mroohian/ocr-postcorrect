package de.iisys.ocr.spellcorrector.trigram;

import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.spellcorrector.trigram.lattice.SpellCorrectorTrigramLatticeNode;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.trigram.ITrigramTransducer;
import de.iisys.ocr.transducer.trigram.lattice.ITrigramLatticeNode;
import de.iisys.ocr.transducer.trigram.lattice.ITrigramLatticeNodeList;
import de.iisys.ocr.transducer.trigram.lattice.TrigramLatticeNodeList;
import de.iisys.ocr.types.DoubleVector;
import de.iisys.ocr.types.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SpellCorrectorTrigramTransducer
 * de.iisys.ocr.spellcorrector.trigram
 * Created by reza on 15.10.14.
 */
public class SpellCorrectorTrigramTransducer implements ITrigramTransducer {
    private final TokenCorpa mCorpa;
    private final double mMaxDist;
    private final List<IFeature> mFeatures;
    private final DoubleVector mLambda;

    public SpellCorrectorTrigramTransducer(TokenCorpa corpa, double maxDist, Map<IFeature, Double> featuresMap) {
        mCorpa = corpa;
        mMaxDist = maxDist;
        mFeatures = new ArrayList<IFeature>(featuresMap.keySet());
        mLambda = new DoubleVector(featuresMap.values());
    }

    @SuppressWarnings("UnusedDeclaration")
    public Corpa getCorpa() {
        return mCorpa;
    }

    @SuppressWarnings("UnusedDeclaration")
    public double getMaxDist() {
        return mMaxDist;
    }

    @Override
    public ITrigramLatticeNodeList buildCandidateList(int sequencePosition, int sequenceLength, String input, ITrigramLatticeNodeList prevList) {
        Map<Double, List<INode>> results = mCorpa.findSimilarWords(input, mMaxDist);

        ITrigramLatticeNodeList candidateList = new TrigramLatticeNodeList(sequencePosition, sequenceLength);
        for (int dist = 0; dist <= mMaxDist * 10; dist++) {
            List<INode> values  = results.get(dist / 10.0);
            //if (values.size() > 0) System.err.print("(Dist=" + dist + ") ");
            for (INode value : values) {
                Token candidateToken = (Token) value;
                candidateList.add(new SpellCorrectorTrigramLatticeNode(candidateToken, prevList, dist));
                //System.err.print(candidateToken.getWord() + ", ");
            }
            //System.err.println();
        }

        if (candidateList.size() == 0) {
            int wordId = mCorpa.addWord(input);
            Token candidateToken = new Token(wordId);
            candidateList.add(new SpellCorrectorTrigramLatticeNode(candidateToken, prevList, 0));
        }

        return candidateList;
    }

    @Override
    public ILatticeNode buildLatticeNode(INode output) {
        return new SpellCorrectorTrigramLatticeNode((Token)output, null, 0);
    }

    @Override
    public int getFeatureCount() {
        return mFeatures.size();
    }

    @Override
    public List<IFeature> getFeatures() {
        return mFeatures;
    }

    @Override
    public DoubleVector getParamsVector() {
        return mLambda;
    }

    @Override
    public double getLogCliqueValue(int j, Object[] extras, String[] inputs, ITrigramLatticeNode prevNode2, ITrigramLatticeNode prevNode1, ITrigramLatticeNode node) {
        assert inputs != null;
        assert prevNode2 != null;
        assert prevNode1 != null;
        assert node != null;

        double logValue = 0.0;

        for (int i = 0; i < mFeatures.size(); i++) {
            logValue += mLambda.get(i) * getFeatureValue(i, prevNode2, prevNode1, node, inputs, extras, j);
        }

        //System.out.println("log Ψ_" + (j+1) + "(x, " + prevNode2.getNode().getWord() + ", " + prevNode1.getNode().getWord() + ", " + node.getNode().getWord()+ ") =\t" + logValue);

        return logValue;
    }

    @Override
    public double getFeatureValue(int i, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node, String[] inputs, Object[] extras, int j) {
        return mFeatures.get(i).computeValue(extras, inputs, j, prevNode2, prevNode1, node);
    }

}
