package de.iisys.ocr.test.core.graph;

import de.iisys.ocr.graph.GraphDB;
import de.iisys.ocr.graph.RemoteGraphDB;
import de.iisys.ocr.types.IDisposable;
import org.junit.AfterClass;

import java.io.IOException;

/**
 * RemoteGraphDBBaseTest
 * de.iisys.ocr.test.core.graph
 * Created by reza on 04.09.14.
 */
public class RemoteGraphDBUnit  implements IDisposable {
    protected static GraphDB mGraphDB;

    public RemoteGraphDBUnit(String server, String dbName, String user, String password, boolean useTransactionalDB) throws IOException {
        mGraphDB = new RemoteGraphDB(server, dbName, user, password);

        if (useTransactionalDB) {
            mGraphDB.setTransactionalMode(true);
        } else {
            mGraphDB.setTransactionalMode(false);
        }
    }

    @AfterClass
    public static void tearDown() throws IOException {
        mGraphDB.dispose();
    }

    public GraphDB getGraphDB() {
        return mGraphDB;
    }

    @Override
    public void dispose() {
        mGraphDB.dispose();
    }
}
