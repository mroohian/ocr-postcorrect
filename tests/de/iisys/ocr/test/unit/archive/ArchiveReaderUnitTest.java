package de.iisys.ocr.test.unit.archive;

import de.iisys.ocr.archive.ArchiveReader;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.archive.ArchiveReaderUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * ArchiveReaderUnitTest
 * de.iisys.ocr.test.unit.archive
 * Created by reza on 19.08.14.
 */
public class ArchiveReaderUnitTest extends BaseTest {
    private static ArchiveReaderUnit mArchiveReaderUnit;

    @BeforeClass
    public static void setup() {
        mArchiveReaderUnit = new ArchiveReaderUnit(mArchivePath);
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testReadDir() throws Exception {
        ArchiveReader archiveReader = new ArchiveReader(new ArchiveReader.IArchiveReaderCallback() {
            @Override
            public void processFileContent(ArchiveReader archiveReader1, File file) {
                assert file != null;

                try {
                    ArchiveReader.readRawFile(file);
                } catch (IOException e) {
                    System.out.println("cannot read file: " + file.getAbsolutePath());
                    assert false;
                    return;
                }

                System.out.println("successfully read: " + file.getAbsolutePath());
                archiveReader1.cancelOperation();
            }

            @Override
            public void processDirChanged(File dir) {
            }
        });

        archiveReader.readDir(new File(mArchiveReaderUnit.getPath()));
    }
}
