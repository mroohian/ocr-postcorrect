package de.iisys.ocr.test.core.graph;

import de.iisys.ocr.test.core.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;

/**
 * EmptyLocalGraphDBTest
 * de.iisys.ocr.test.core.graph
 * Created by reza on 04.09.14.
 */
public abstract class EmptyLocalGraphDBTest extends BaseTest {
    protected static LocalGraphDBUnit mGraphDbUnit;

    @BeforeClass
    public static void setup() throws IOException {
        FileUtils.deleteDirectory(new File(mEmptyGraphDBPath));
        mGraphDbUnit = new LocalGraphDBUnit(mEmptyGraphDBPath, mEmptyGraphUser, mEmptyGraphPassword, false);
    }

    @AfterClass
    public static void tearDown() {
        mGraphDbUnit.dispose();
        try {
            Thread.sleep(5000);
            FileUtils.deleteDirectory(new File(mEmptyGraphDBPath));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
