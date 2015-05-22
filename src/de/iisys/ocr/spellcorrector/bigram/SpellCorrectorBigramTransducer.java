package de.iisys.ocr.spellcorrector.bigram;

import de.iisys.ocr.spellcorrector.bigram.lattice.SpellCorrectorBigramLatticeNode;
import de.iisys.ocr.transducer.bigram.IBigramTransducer;
import de.iisys.ocr.transducer.bigram.lattice.BigramLatticeNodeList;
import de.iisys.ocr.transducer.bigram.lattice.IBigramLatticeNode;
import de.iisys.ocr.transducer.bigram.lattice.IBigramLatticeNodeList;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.types.DoubleVector;
import de.iisys.ocr.types.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SpellCorrectorBigramTransducer
 * de.iisys.ocr.spellcorrector.bigram
 * Created by reza on 28.07.14.
 */
public class SpellCorrectorBigramTransducer implements IBigramTransducer {
    private final Corpa mCorpa;
    private final double mMaxDist;
    private final List<IFeature> mFeatures;
    private final DoubleVector mLambda;

    public SpellCorrectorBigramTransducer(Corpa corpa, double maxDist, Map<IFeature, Double> featuresMap) {
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
    public IBigramLatticeNodeList buildCandidateList(int sequencePosition, int sequenceLength, String input) {
        Map<Double, List<INode>> results = mCorpa.findSimilarWords(input, mMaxDist);

        IBigramLatticeNodeList candidateList = new BigramLatticeNodeList(sequencePosition, sequenceLength);
        for (int dist = 0; dist <= mMaxDist * 10; dist++) {
            List<INode> values  = results.get(dist / 10.0);
            //if (values.size() > 0) System.err.print("(Dist=" + dist + ") ");
            for (INode value : values) {
                Token candidateToken = (Token) value;
                candidateList.add(new SpellCorrectorBigramLatticeNode(candidateToken, dist));
                //System.err.print(candidateToken.getWord() + ", ");
            }
            //System.err.println();
        }

        return candidateList;
    }

    @Override
    public ILatticeNode buildLatticeNode(INode output) {
        return new SpellCorrectorBigramLatticeNode((Token)output, 0);
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
    public double getLogCliqueValue(int j, Object[] extras, String[] inputs, IBigramLatticeNode prevNode, IBigramLatticeNode node) {
        assert inputs != null;
        assert prevNode != null;
        assert node != null;

        double logValue = 0.0;

        for (int i = 0; i < mFeatures.size(); i++) {
            logValue += mLambda.get(i) * getFeatureValue(i, prevNode, node, inputs, extras, j);
        }

        //System.out.println("log Ψ_" + (j+1) + "(x, " + prevNode.getNode().getWord() + ", " + node.getNode().getWord()+ ") =\t" + logValue);

        return logValue;
    }

    @Override
    public double getFeatureValue(int i, ILatticeNode prevNode, ILatticeNode node, String[] inputs, Object[] extras, int j) {
        return mFeatures.get(i).computeValue(extras, inputs, j, null, prevNode, node);
    }
}
