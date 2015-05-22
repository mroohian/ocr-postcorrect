package de.iisys.ocr.possequence;

import de.iisys.levdistcorpa.types.EndNode;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.levdistcorpa.types.StartNode;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.possequence.feature.core.IFeature;
import de.iisys.ocr.possequence.lattice.LatticeNode;
import de.iisys.ocr.possequence.property.core.IProperty;
import de.iisys.ocr.types.SparseVector;
import de.iisys.ocr.types.WordPOSInfo;

import java.util.*;

/**
 * POSSequenceTrigramTransducer
 * Created by reza on 12.01.15.
 */
public class POSSequenceTrigramTransducer {
    private final TokenCorpa corpa;
    private final double maxDist;
    private final List<IProperty> properties;
    private final List<IFeature> features;
    private final List<Double> weights;

    public POSSequenceTrigramTransducer(TokenCorpa corpa, double maxDist, List<IProperty> properties,
                                        Map<IFeature,Double> featuresMap) {
        this.corpa = corpa;
        this.maxDist = maxDist;
        this.properties = properties;
        this.features = new ArrayList<IFeature>(featuresMap.keySet());
        this.weights = new ArrayList<Double>(featuresMap.values());
    }

    public POSSequenceTrigramTransducer(TokenCorpa corpa, double maxDist, List<IProperty> properties) {
        this.corpa = corpa;
        this.maxDist = maxDist;
        this.properties = properties;
        this.features = null;
        this.weights = null;
    }

    public double getLogCliqueValue(short prev2, short prev1, short current, SparseVector[] inputs, int j) {
        double logSum = 0.0;

        for (int i = 0; i < features.size(); i++) {
            IFeature feature = features.get(i);
            logSum += weights.get(i) * feature.getLogValue(prev2, prev1, current, inputs, j);
        }

        return logSum;
    }
    public double getLogCliqueValue(LatticeNode s2, LatticeNode s1, LatticeNode s, SparseVector[] inputs, int j) {
        return getLogCliqueValue(s2.getPosID(), s1.getPosID(), s.getPosID(), inputs, j);
    }

    public double getFeatureValue(int i, short prev2, short prev1, short current, SparseVector[] inputs, int j) {
        return features.get(i).getLogValue(prev2, prev1, current, inputs, j);
    }

    public double getFeatureLogValue(int i, LatticeNode prevNode2, LatticeNode prevNode1, LatticeNode currentNode,
                                     SparseVector[] inputs, int j) {
        return getFeatureValue(i, prevNode2.getPosID(), prevNode1.getPosID(), currentNode.getPosID(), inputs, j);
    }

    public Set<INode> buildCandidateList(String input) {
        Map<Double, List<INode>> results = corpa.findSimilarWords(input, maxDist);
        Set<INode> candidateList = new HashSet<INode>();
        for (int dist = 0; dist <= maxDist * 10; dist++) {
            List<INode> values = results.get(dist / 10.0);
            candidateList.addAll(values);
        }

        return candidateList;
    }

    public Set<Short> buildCandidatePOSList(Set<INode> candidateList) {
        // Get the list of possible POS tags
        Set<Short> posCandidates = new HashSet<Short>();
        for (INode node : candidateList) {
            if (node instanceof WordPOSInfo) {
                WordPOSInfo wordPOSInfo = (WordPOSInfo)node;
                posCandidates.addAll(wordPOSInfo.getPosTagIDs());
            } else if (node instanceof StartNode) {
                posCandidates.add(StartNode.SHORT_INDEX);
            } else if (node instanceof EndNode) {
                posCandidates.add(EndNode.SHORT_INDEX);
            }
        }

        return posCandidates;
    }

    public Set<Short> buildCandidatePOSList(String input) {
        return buildCandidatePOSList(buildCandidateList(input));
    }

    public int getFeatureCount() {
        return features.size();
    }

    public double getFeatureWeights(int i) {
        return weights.get(i);
    }

    public void setFeatureWeights(int i, double featureWeight) {
        weights.set(i, featureWeight);
    }

    public SparseVector[] createPropertiesVector(List<String> input) {
        SparseVector[] result = new SparseVector[input.size()];
        for (int j = 0; j < input.size(); j++) {
            result[j] = new SparseVector(properties.size());
        }

        for (int i = 0; i < properties.size(); i++) {
            IProperty property = properties.get(i);
            int[] propertyValue = property.getValue(input);

            for (int j = 0; j < input.size(); j++) {
                int value = propertyValue[j];
                if (value != 0) {
                    result[j].put(i, value);
                }
            }
        }

        return result;
    }

    public static int getPropertyIndex(List<IProperty> properties, Class<? extends IProperty> propertyClass) {
        int index = -1;

        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).getClass().equals(propertyClass)) {
                index = i;
                break;
            }
        }

        return index;
    }


}
