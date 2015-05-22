package de.iisys.ocr.test.core.graph;

import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import de.iisys.ocr.graph.GraphDB;
import de.iisys.ocr.graph.LocalGraphDB;
import de.iisys.ocr.types.IDisposable;

/**
 * LocalGraphDBUnit
 * de.iisys.ocr.test.core.graph
 * Created by reza on 04.09.14.
 */
public class LocalGraphDBUnit implements IDisposable {
    private GraphDB mGraphDB;

    public LocalGraphDBUnit(String dbPath, String user, String password, boolean useTransactionalDB) {
        mGraphDB = new LocalGraphDB(dbPath, user, password);

        if (useTransactionalDB) {
            mGraphDB.setTransactionalMode(true);
        } else {
            mGraphDB.setTransactionalMode(false);
        }

    }

    public GraphDB getGraphDB() {
        return mGraphDB;
    }

    @Override
    public void dispose() {
        mGraphDB.dispose();
    }

    public OrientBaseGraph getOrientGraph() {
        return mGraphDB.getOrientGraph();
    }
}
