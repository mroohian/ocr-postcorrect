package de.iisys.ocr.graph;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import de.iisys.ocr.tokenizer.ITokenizer;
import de.iisys.ocr.types.IDisposable;

import java.util.Collection;

/**
 * GraphDB
 * de.iisys.ocr.graph
 * Created by reza on 25.07.14.
 */
public abstract class GraphDB implements IDisposable {
    private OrientBaseGraph mGraph;

    public static final String VERTEX_TYPE_WORD = "Word";
    public static final String VERTEX_TYPE_PROPERTY_VALUE = "value";
    public static final String VERTEX_TYPE_PROPERTY_FREQ = "freq";
    public static final String INDEX_WORD_BY_VALUE = "indexWordValue";

    public static final String EDGE_TYPE_FOLLOWED_BY = "followedBy";
    public static final String EDGE_TYPE_FOLLOWED_BY_PROPERTY_IN = "in";
    public static final String EDGE_TYPE_FOLLOWED_BY_PROPERTY_OUT = "out";
    public static final String EDGE_TYPE_FOLLOWED_BY_PROPERTY_FREQ = "freq";
    public static final String INDEX_FOLLOWED_BY_IN_OUT = "indexFollowedBy_in_out";
    public static final String INDEX_FOLLOWED_BY_IN = "indexFollowedBy_in";
    public static final String INDEX_FOLLOWED_BY_OUT = "indexFollowedBy_out";

    public static final String EDGE_TYPE_FOLLOWED_BY_DIST_ONE = "followedByDist1";
    public static final String EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_IN = "in";
    public static final String EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_OUT = "out";
    public static final String EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_FREQ = "freq";
    public static final String INDEX_FOLLOWED_BY_DIST_ONE_IN_OUT = "indexFollowedByDist1_in_out";
    public static final String INDEX_FOLLOWED_BY_DIST_ONE_IN = "indexFollowedByDist1_in";
    public static final String INDEX_FOLLOWED_BY_DIST_ONE_OUT = "indexFollowedByDist1_out";

    private final OrientGraphFactory mGraphFactory;

    private boolean mIsTransactionalMode;

    public GraphDB(String server, String user, String password) {
        mGraphFactory = new OrientGraphFactory(server, user, password);
        if (!mGraphFactory.exists()) {
            throw new RuntimeException("Database does not exist.");
        }
        OrientBaseGraph graph = mGraphFactory.getNoTx();
        fixGraphDBSchema(graph);

        mGraph = graph;
        mIsTransactionalMode = false;
    }

    protected abstract boolean fixGraphDBSchema(OrientBaseGraph graph);

    protected abstract OIdentifiable getWordByValue(String value);
    protected abstract Collection<OIdentifiable> getFollowingWords(OIdentifiable id);
    protected abstract Collection<OIdentifiable> getFollowingDist1Words(OIdentifiable id);
    protected abstract Collection<OIdentifiable> getPrecedingWords(OIdentifiable id);
    protected abstract Collection<OIdentifiable> getPrecedingDist1Words(OIdentifiable id);
    protected abstract OIdentifiable getFollowedByEdgeByKey(OCompositeKey key);
    protected abstract OIdentifiable getFollowedByDist1EdgeByKey(OCompositeKey key);

    public boolean isTransactionalMode() {
        return mIsTransactionalMode;
    }

    public void setTransactionalMode(boolean transactionalMode) {
        if (transactionalMode == mIsTransactionalMode) return;
        mIsTransactionalMode = transactionalMode;

        if (transactionalMode) {
            mGraph = mGraphFactory.get();
        } else {
            mGraph = mGraphFactory.getNoTx();
        }
    }

    public boolean isOpen() {
        return mGraph != null && !mGraph.isClosed();
    }

    public void dispose() {
        mGraph.shutdown();
    }

    public OrientBaseGraph getOrientGraph() {
        return mGraph;
    }

    public OIdentifiable getWordRID(String value) {
        if (value == null) return null;
        return getWordByValue(value);
    }

    public Vertex getWord(String value) {
        OIdentifiable recordId = getWordRID(value);
        if (recordId == null) return null;
        return mGraph.getVertex(recordId);
    }

    public OIdentifiable getFollowedByEdgeRID(OCompositeKey key) {
        if (key == null) return null;
        return getFollowedByEdgeByKey(key);
    }

    public OIdentifiable getFollowedByEdgeRID(OIdentifiable recordId1, OIdentifiable recordId2) {
        if (recordId1 == null || recordId2 == null) return null;
        OCompositeKey key = new OCompositeKey(recordId1, recordId2);
        return getFollowedByEdgeRID(key);
    }

    public Edge getFollowedByEdge(Vertex w1, Vertex w2) {
        if (w1 == null || w2 == null) return null;
        OCompositeKey key = new OCompositeKey(w2.getId(), w1.getId());
        OIdentifiable edgeId = getFollowedByEdgeRID(key);
        if (edgeId == null) return null;
        return mGraph.getEdge(edgeId);
    }

    public OIdentifiable getFollowedByDist1EdgeRID(OCompositeKey key) {
        if (key == null) return null;
        return getFollowedByDist1EdgeByKey(key);
    }

    public OIdentifiable getFollowedByDist1EdgeRID(OIdentifiable recordId1, OIdentifiable recordId2) {
        if (recordId1 == null || recordId2 == null) return null;
        OCompositeKey key = new OCompositeKey(recordId1, recordId2);
        return getFollowedByDist1EdgeRID(key);
    }

    public Edge getFollowedByDist1Edge(Vertex w1, Vertex w2) {
        if (w1 == null || w2 == null) return null;
        OCompositeKey key = new OCompositeKey(w2.getId(), w1.getId());
        OIdentifiable edgeId = getFollowedByDist1EdgeRID(key);
        if (edgeId == null) return null;
        return mGraph.getEdge(edgeId);
    }

    public void printFollowed(Vertex w) {
        System.out.println("\nprintFollowed");
        for (Edge e : w.getEdges(Direction.OUT, EDGE_TYPE_FOLLOWED_BY)) {
            System.out.println(e + "\n\t" + e.getVertex(Direction.IN) + " " + e.getVertex(Direction.IN).getProperty("value") + "(" + e.getProperty("freq") + ")");
        }
    }

    public void printFollowing(Vertex w) {
        System.out.println("\nprintFollowing");
        for (Edge e : w.getEdges(Direction.IN, EDGE_TYPE_FOLLOWED_BY)) {
            System.out.println(e + "\n\t" + e.getVertex(Direction.OUT) + " " + e.getVertex(Direction.OUT).getProperty("value") + "(" + e.getProperty("freq") + ")");
        }
    }

    @SuppressWarnings("unused")
    public void printWord(Vertex w) {
        System.out.println("Word: " + w.getProperty("value") + " (" + w.getProperty("freq") + ")");

        printFollowed(w);
        printFollowing(w);

        System.out.println("");
    }

    @SuppressWarnings("unused")
    public void printAllWords() {
        System.out.println("Words:");
        for (Vertex v : mGraph.getVertices()) {
            System.out.println("\t- " + v.getProperty("value") + " (" + v.getProperty("freq") + ")");
        }
    }

    @SuppressWarnings("unused")
    public void populateDataFollowedBy(ITokenizer tokenizer) {
        Vertex current = null, before;

        for (String part; tokenizer.hasNext(); ) {
            part = tokenizer.next();
            before = current != null ? mGraph.getVertex(current.getId()) : null;
            current = getWord(part);
            if (current == null) {
                current = mGraph.addVertex("class:Word", "value", part, "freq", 1);
                assert(current != null);
            } else {
                current.setProperty("freq", (Integer)current.getProperty("freq") + 1);
            }

            // Usage: addEdge(null, out, in, EDGE_TYPE_FOLLOWED_BY);
            if (before != null) {
                Edge edge1 = getFollowedByEdge(current, before);
                if (edge1 == null) {
                    edge1 = mGraph.addEdge(null, current, before, EDGE_TYPE_FOLLOWED_BY);
                    assert (edge1 != null);
                    edge1.setProperty("freq", 1);
                } else {
                    edge1.setProperty("freq", (Integer)edge1.getProperty("freq") + 1);
                }
            }

            mGraph.commit();
        }
        //mGraph.commit();
    }

    @SuppressWarnings("unused")
    public void populateDataComplete(ITokenizer tokenizer) {
        Vertex current = null, before = null, before2;

        for (String part; tokenizer.hasNext(); ) {
            part = tokenizer.next();
            before2 = before != null ? mGraph.getVertex(before.getId()) : null;
            before = current != null ? mGraph.getVertex(current.getId()) : null;
            current = getWord(part);
            if (current == null) {
                current = mGraph.addVertex("class:Word", "value", part, "freq", 1);
                assert(current != null);
            } else {
                current.setProperty("freq", (Integer)current.getProperty("freq") + 1);
            }

            // Usage: addEdge(null, out, in, EDGE_TYPE_FOLLOWED_BY);
            if (before != null) {
                Edge edge1 = getFollowedByEdge(current, before);
                if (edge1 == null) {
                    edge1 = mGraph.addEdge(null, current, before, EDGE_TYPE_FOLLOWED_BY);
                    assert (edge1 != null);
                    edge1.setProperty("freq", 1);
                } else {
                    edge1.setProperty("freq", (Integer)edge1.getProperty("freq") + 1);
                }

                if (before2 != null) {
                    Edge edge2 = getFollowedByDist1Edge(current, before2);

                    if (edge2 == null) {
                        edge2 = mGraph.addEdge(null, current, before2, EDGE_TYPE_FOLLOWED_BY_DIST_ONE);
                        assert (edge2 != null);
                        edge2.setProperty("freq", 1);
                    } else {
                        edge2.setProperty("freq", (Integer)edge2.getProperty("freq") + 1);
                    }
                }
            }

            mGraph.commit();
        }
        //mGraph.commit();
    }

    public boolean hasData() {
        // At least one object
        return mGraph.getVertices().iterator().hasNext();
    }
}
