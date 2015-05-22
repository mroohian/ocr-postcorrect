package de.iisys.ocr.transducer.trigram.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.transducer.lattice.LatticeNode;

import java.util.HashMap;
import java.util.Map;

/**
 * TrigramLatticeNode
 * de.iisys.ocr.transducer.trigram.lattice
 * Created by reza on 15.10.14.
 */
public abstract  class TrigramLatticeNode extends LatticeNode implements ITrigramLatticeNode {
    private static ITrigramLatticeNode nullObject = new TrigramLatticeNode(null, null) {
        @Override
        public String getDetails() {
            return null;
        }
    };

    private class TrigramLatticeNodeData {
        // forward-backward
        private double mAlpha; // log(alpha)
        private double mBeta; // log(beta)

        // viterbi
        private double mDelta; // log(delta)
        private ITrigramLatticeNode mPrevPsiNode;
    }

    private class NoNullHashMap extends HashMap<ITrigramLatticeNode, TrigramLatticeNodeData> {
        public NoNullHashMap(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public TrigramLatticeNodeData put(ITrigramLatticeNode key, TrigramLatticeNodeData value) {
            if (key == null) key = nullObject;
            return super.put(key, value);
        }

        @Override
        public TrigramLatticeNodeData get(Object key) {
            if (key == null) key = nullObject;
            return super.get(key);
        }
    }

    private final Map<ITrigramLatticeNode, TrigramLatticeNodeData> mNodesData;

    public TrigramLatticeNode(INode token, ITrigramLatticeNodeList prevList) {
        this(token, prevList, false);
    }

    public TrigramLatticeNode(INode token, ITrigramLatticeNodeList prevList, boolean isBoundary) {
        super(token, isBoundary);

        if (token == null) {
            // Null object
            mNodesData = null;
        } else {
            if (prevList != null) {
                mNodesData = new NoNullHashMap(prevList.size());

                for (ITrigramLatticeNode latticeNode : prevList) {
                    mNodesData.put(latticeNode, new TrigramLatticeNodeData());
                }
            } else {
                mNodesData = new NoNullHashMap(1);
                mNodesData.put(null, new TrigramLatticeNodeData());
            }
        }
    }

    @Override
    public double getAlpha(ITrigramLatticeNode prevNode) {
        return mNodesData.get(prevNode).mAlpha;
    }

    @Override
    public void setAlpha(ITrigramLatticeNode prevNode, double alpha) {
        mNodesData.get(prevNode).mAlpha = alpha;
    }

    @Override
    public double getBeta(ITrigramLatticeNode prevNode) {
        return mNodesData.get(prevNode).mBeta;
    }

    @Override
    public void setBeta(ITrigramLatticeNode prevNode, double logBeta) {
        mNodesData.get(prevNode).mBeta = logBeta;
    }

    @Override
    public double getDelta(ITrigramLatticeNode prevNode) {
        return mNodesData.get(prevNode).mDelta;
    }

    @Override
    public void setDelta(ITrigramLatticeNode prevNode, double logDelta) {
        mNodesData.get(prevNode).mDelta = logDelta;
    }

    @Override
    public ITrigramLatticeNode getPrevPsiNode(ITrigramLatticeNode prevNode) {
        return mNodesData.get(prevNode).mPrevPsiNode;
    }

    @Override
    public void setPrevPsiNode(ITrigramLatticeNode prevNode, ITrigramLatticeNode prevPsiNode) {
        mNodesData.get(prevNode).mPrevPsiNode = prevPsiNode;
    }
}
