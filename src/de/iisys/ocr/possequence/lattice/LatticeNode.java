package de.iisys.ocr.possequence.lattice;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * ViterbiNode
 * Created by reza on 23.01.15.
 */
public class LatticeNode {
    private final Map<LatticeNode, NodeData> nodesData;
    private final short posID;

    public LatticeNode(short posID, Collection<LatticeNode> prevList) {
        this.posID = posID;

        if (prevList != null) {
            nodesData = new HashMap<LatticeNode, NodeData>();

            for (LatticeNode prevNode : prevList) {
                nodesData.put(prevNode, new NodeData());
            }
        } else {
            nodesData = null;
        }
    }

    public void clear() {
        if (nodesData != null) {
            for (NodeData nodeData : nodesData.values()) {
                nodeData.clear();
            }
        }
    }

    public short getPosID() {
        return posID;
    }

    public void setLogAlpha(LatticeNode prevNode, double logAlpha) {
        NodeData nodesInfo = nodesData.get(prevNode);
        nodesInfo.setLogAlpha(logAlpha);
    }

    public double getLogAlpha(LatticeNode prevNode) {
        NodeData nodesInfo = nodesData.get(prevNode);
        return nodesInfo.getLogAlpha();
    }

    public void setLogBeta(LatticeNode prevNode, double logAlpha) {
        NodeData nodesInfo = nodesData.get(prevNode);
        nodesInfo.setLogBeta(logAlpha);
    }

    public double getLogBeta(LatticeNode prevNode) {
        NodeData nodesInfo = nodesData.get(prevNode);
        return nodesInfo.getLogBeta();
    }

    public void setLogDelta(LatticeNode prevNode, double logDelta) {
        NodeData nodesInfo = nodesData.get(prevNode);
        nodesInfo.setLogDelta(logDelta);
    }

    public double getLogDelta(LatticeNode prevNode) {
        NodeData nodesInfo = nodesData.get(prevNode);
        return nodesInfo.getLogDelta();
    }

    public void setPrevPsiNode(LatticeNode prevNode, LatticeNode prevPsiNode) {
        NodeData nodesInfo = nodesData.get(prevNode);
        nodesInfo.setPsiNode(prevPsiNode);
    }

    public LatticeNode getPrevPsiNode(LatticeNode prevNode) {
        NodeData nodesInfo = nodesData.get(prevNode);
        return nodesInfo.getPsiNode();
    }

    private class NodeData {
        private double logAlpha;
        private double logBeta;
        private double logDelta; // log(delta)
        private LatticeNode psiNode;

        public void setLogAlpha(double logAlpha) {
            this.logAlpha = logAlpha;
        }

        public double getLogAlpha() {
            return logAlpha;
        }

        public void setLogBeta(double logBeta) {
            this.logBeta = logBeta;
        }

        public double getLogBeta() {
            return logBeta;
        }

        public void setLogDelta(double logDelta) {
            this.logDelta = logDelta;
        }

        public double getLogDelta() {
            return logDelta;
        }

        public void setPsiNode(LatticeNode psiNode) {
            this.psiNode = psiNode;
        }

        public LatticeNode getPsiNode() {
            return psiNode;
        }

        public void clear() {
            logAlpha = logBeta = logDelta = 0.0;
            psiNode = null;
        }
    }
}
