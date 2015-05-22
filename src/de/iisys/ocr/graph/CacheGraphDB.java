package de.iisys.ocr.graph;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;

import java.util.HashMap;
import java.util.Map;

/**
 * de.iisys.ocr.graph
 * Created by reza on 04.09.14.
 */
public abstract class CacheGraphDB extends GraphDB {
    private final Map<String, OIdentifiable> mWordMap = new HashMap<String, OIdentifiable>();
    private final Map<String, OIdentifiable> mFollowedByEdgeMap = new HashMap<String, OIdentifiable>();
    private final Map<String, OIdentifiable> mFollowedByDist1EdgeMap = new HashMap<String, OIdentifiable>();

    public CacheGraphDB(String server, String user, String password) {
        super(server, user, password);
    }

    private OIdentifiable getWordRIDCache(String value) {
        if (!mWordMap.containsKey(value)) return null;
        return mWordMap.get(value);
    }

    private void addWordRIDCache(String value, OIdentifiable recordId) {
        if (mWordMap.containsKey(value)) {
            mWordMap.remove(mWordMap.get(value));
            mWordMap.put(value, recordId);
        } else {
            mWordMap.put(value, recordId);
        }
    }

    private OIdentifiable getFollowedByEdgeRIDCache(String value) {
        if (!mFollowedByEdgeMap.containsKey(value)) return null;
        return mFollowedByEdgeMap.get(value);
    }

    private void addFollowedByEdgeRIDCache(String value, OIdentifiable recordId) {
        if (mFollowedByEdgeMap.containsKey(value)) {
            mFollowedByEdgeMap.remove(mWordMap.get(value));
            mFollowedByEdgeMap.put(value, recordId);
        } else {
            mFollowedByEdgeMap.put(value, recordId);
        }
    }

    private OIdentifiable getFollowedByDist1EdgeRIDCache(String value) {
        if (!mFollowedByDist1EdgeMap.containsKey(value)) return null;
        return mFollowedByDist1EdgeMap.get(value);
    }

    private void addFollowedByDist1EdgeRIDCache(String value, OIdentifiable recordId) {
        if (mFollowedByDist1EdgeMap.containsKey(value)) {
            mFollowedByDist1EdgeMap.remove(mWordMap.get(value));
            mFollowedByDist1EdgeMap.put(value, recordId);
        } else {
            mFollowedByDist1EdgeMap.put(value, recordId);
        }
    }

    @Override
    public OIdentifiable getWordRID(String value) {
        if (value == null) return null;

        OIdentifiable recordId = getWordRIDCache(value);
        if (recordId == null) {
            recordId = super.getWordRID(value);
            if (recordId != null) {
                addWordRIDCache(value, recordId);
            }
        }

        return recordId;
    }

    @Override
    public OIdentifiable getFollowedByEdgeRID(OCompositeKey key) {
        if (key == null) return null;

        final String keyHash = key.toString();
        OIdentifiable recordId = getFollowedByEdgeRIDCache(keyHash);
        if (recordId == null) {
            recordId = super.getFollowedByEdgeRID(key);
            if (recordId != null) {
                addFollowedByEdgeRIDCache(keyHash, recordId);
            }
        }

        return recordId;
    }

    @Override
    public OIdentifiable getFollowedByDist1EdgeRID(OCompositeKey key) {
        if (key == null) return null;

        final String keyHash = key.toString();
        OIdentifiable recordId = getFollowedByDist1EdgeRIDCache(keyHash);
        if (recordId == null) {
            recordId = super.getFollowedByDist1EdgeRID(key);
            if (recordId != null) {
                addFollowedByDist1EdgeRIDCache(keyHash, recordId);
            }
        }

        return recordId;
    }
}
