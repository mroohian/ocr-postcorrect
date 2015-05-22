package de.iisys.ocr.graph;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndexTxAwareMultiValue;
import com.orientechnologies.orient.core.index.OIndexTxAwareOneValue;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdgeType;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

import java.util.Set;

/**
 * de.iisys.ocr.graph
 * Created by reza on 21.08.14.
 */
public class LocalGraphDB extends CacheGraphDB {
    private OrientVertexType mWordType;
    private OrientEdgeType mFollowedByType;
    private OrientEdgeType mFollowedByDist1;
    private OIndexTxAwareOneValue mIndexWordValue;
    private OIndexTxAwareOneValue mIndexFollowedByInOut;
    private OIndexTxAwareMultiValue mIndexFollowedByIn;
    private OIndexTxAwareMultiValue mIndexFollowedByOut;
    private OIndexTxAwareOneValue mIndexFollowedByDist1InOut;
    private OIndexTxAwareMultiValue mIndexFollowedByDist1In;
    private OIndexTxAwareMultiValue mIndexFollowedByDist1Out;

    public LocalGraphDB(String path, String user, String password) {
        super("plocal:" + path, user, password);
    }

    protected boolean fixGraphDBSchema(OrientBaseGraph graph) {
        boolean changed = false;

        mWordType = graph.getVertexType(VERTEX_TYPE_WORD);
        if (mWordType == null) {
            mWordType = graph.createVertexType(VERTEX_TYPE_WORD);
            assert mWordType != null;
            mWordType.createProperty(VERTEX_TYPE_PROPERTY_VALUE, OType.STRING);
            mWordType.createProperty(VERTEX_TYPE_PROPERTY_FREQ, OType.INTEGER);

            // creates a UNIQUE index on value
            mWordType.createIndex(INDEX_WORD_BY_VALUE, OClass.INDEX_TYPE.UNIQUE, VERTEX_TYPE_PROPERTY_VALUE);
            mIndexWordValue = (OIndexTxAwareOneValue) mWordType.getClassIndex(INDEX_WORD_BY_VALUE);
            assert mIndexWordValue != null;

            changed = true;
        } else {
            // Check if index already exists
            mIndexWordValue = (OIndexTxAwareOneValue) mWordType.getClassIndex(INDEX_WORD_BY_VALUE);
            assert mIndexWordValue != null;
        }

        mFollowedByType = graph.getEdgeType(EDGE_TYPE_FOLLOWED_BY);
        if (mFollowedByType == null) {
            mFollowedByType = graph.createEdgeType(EDGE_TYPE_FOLLOWED_BY);
            assert mFollowedByType != null;
            mFollowedByType.createProperty(EDGE_TYPE_FOLLOWED_BY_PROPERTY_FREQ, OType.INTEGER);

            // Manually add in and out links in order to be accessible by index
            mFollowedByType.createProperty(EDGE_TYPE_FOLLOWED_BY_PROPERTY_IN, OType.LINK);
            mFollowedByType.createProperty(EDGE_TYPE_FOLLOWED_BY_PROPERTY_OUT, OType.LINK);

            // create a Unique index on in and out values
            mFollowedByType.createIndex(INDEX_FOLLOWED_BY_IN_OUT, OClass.INDEX_TYPE.UNIQUE, EDGE_TYPE_FOLLOWED_BY_PROPERTY_IN, EDGE_TYPE_FOLLOWED_BY_PROPERTY_OUT);
            mIndexFollowedByInOut = (OIndexTxAwareOneValue) mFollowedByType.getClassIndex(INDEX_FOLLOWED_BY_IN_OUT);
            assert mIndexFollowedByInOut != null;

            // create a non-Unique index only on in values
            mFollowedByType.createIndex(INDEX_FOLLOWED_BY_IN, OClass.INDEX_TYPE.NOTUNIQUE, EDGE_TYPE_FOLLOWED_BY_PROPERTY_IN);
            mIndexFollowedByIn = (OIndexTxAwareMultiValue) mFollowedByType.getClassIndex(INDEX_FOLLOWED_BY_IN);
            assert mIndexFollowedByIn != null;

            // create a non-Unique index only on out values
            mFollowedByType.createIndex(INDEX_FOLLOWED_BY_OUT, OClass.INDEX_TYPE.NOTUNIQUE, EDGE_TYPE_FOLLOWED_BY_PROPERTY_OUT);
            mIndexFollowedByOut = (OIndexTxAwareMultiValue) mFollowedByType.getClassIndex(INDEX_FOLLOWED_BY_OUT);
            assert mIndexFollowedByOut != null;

            changed = true;
        } else {
            // Check if index already exists
            mIndexFollowedByInOut = (OIndexTxAwareOneValue) mFollowedByType.getClassIndex(INDEX_FOLLOWED_BY_IN_OUT);
            assert mIndexFollowedByInOut != null;

            mIndexFollowedByIn = (OIndexTxAwareMultiValue) mFollowedByType.getClassIndex(INDEX_FOLLOWED_BY_IN);
            assert mIndexFollowedByIn != null;

            mIndexFollowedByOut = (OIndexTxAwareMultiValue) mFollowedByType.getClassIndex(INDEX_FOLLOWED_BY_OUT);
            assert mIndexFollowedByOut != null;
        }

        mFollowedByDist1 = graph.getEdgeType(EDGE_TYPE_FOLLOWED_BY_DIST_ONE);
        if (mFollowedByDist1 == null) {
            mFollowedByDist1 = graph.createEdgeType(EDGE_TYPE_FOLLOWED_BY_DIST_ONE);
            assert mFollowedByDist1 != null;
            mFollowedByDist1.createProperty(EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_FREQ, OType.INTEGER);

            // Manually add in and out links in order to be accessible by index
            mFollowedByDist1.createProperty(EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_IN, OType.LINK);
            mFollowedByDist1.createProperty(EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_OUT, OType.LINK);

            // create a Unique index on in and out values
            mFollowedByDist1.createIndex(INDEX_FOLLOWED_BY_DIST_ONE_IN_OUT, OClass.INDEX_TYPE.UNIQUE, EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_IN, EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_OUT);
            mIndexFollowedByDist1InOut = (OIndexTxAwareOneValue) mFollowedByDist1.getClassIndex(INDEX_FOLLOWED_BY_DIST_ONE_IN_OUT);
            assert mIndexFollowedByDist1InOut != null;

            // create a non-Unique index only on in values
            mFollowedByDist1.createIndex(INDEX_FOLLOWED_BY_DIST_ONE_IN, OClass.INDEX_TYPE.NOTUNIQUE, EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_IN);
            mIndexFollowedByDist1In = (OIndexTxAwareMultiValue) mFollowedByDist1.getClassIndex(INDEX_FOLLOWED_BY_DIST_ONE_IN);
            assert mIndexFollowedByDist1In != null;

            // create a non-Unique index only on out values
            mFollowedByDist1.createIndex(INDEX_FOLLOWED_BY_DIST_ONE_OUT, OClass.INDEX_TYPE.NOTUNIQUE, EDGE_TYPE_FOLLOWED_BY_DIST_ONE_PROPERTY_OUT);
            mIndexFollowedByDist1Out = (OIndexTxAwareMultiValue) mFollowedByDist1.getClassIndex(INDEX_FOLLOWED_BY_DIST_ONE_OUT);
            assert mIndexFollowedByDist1Out != null;

            changed = true;
        } else {
            // Check if index already exists
            mIndexFollowedByDist1InOut = (OIndexTxAwareOneValue) mFollowedByDist1.getClassIndex(INDEX_FOLLOWED_BY_DIST_ONE_IN_OUT);
            assert mIndexFollowedByDist1InOut != null;

            mIndexFollowedByDist1In = (OIndexTxAwareMultiValue) mFollowedByDist1.getClassIndex(INDEX_FOLLOWED_BY_DIST_ONE_IN);
            assert mIndexFollowedByDist1In != null;

            mIndexFollowedByDist1Out = (OIndexTxAwareMultiValue) mFollowedByDist1.getClassIndex(INDEX_FOLLOWED_BY_DIST_ONE_OUT);
            assert mIndexFollowedByDist1Out != null;
        }

        return changed;
    }

    @Override
    protected Set<OIdentifiable> getFollowingWords(OIdentifiable id) {
        return mIndexFollowedByIn.get(id);
    }

    @Override
    protected Set<OIdentifiable> getFollowingDist1Words(OIdentifiable id) {
        return mIndexFollowedByDist1In.get(id);
    }

    @Override
    protected Set<OIdentifiable> getPrecedingWords(OIdentifiable id) {
        return mIndexFollowedByOut.get(id);
    }

    @Override
    protected Set<OIdentifiable> getPrecedingDist1Words(OIdentifiable id) {
        return mIndexFollowedByDist1Out.get(id);
    }

    @Override
    protected OIdentifiable getWordByValue(String value) {
        return mIndexWordValue.get(value);
    }

    @Override
    protected OIdentifiable getFollowedByEdgeByKey(OCompositeKey key) {
        return mIndexFollowedByInOut.get(key);
    }

    @Override
    protected OIdentifiable getFollowedByDist1EdgeByKey(OCompositeKey key) {
        return mIndexFollowedByDist1InOut.get(key);
    }
}
