package de.iisys.ocr.test.performance.archive;

import de.iisys.ocr.archive.ArchiveReader;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.test.core.archive.ArchiveReaderUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * de.iisys.ocr.test.performance.archive
 * Created by reza on 19.08.14.
 */
public class ArchiveReaderPerformanceTest extends BaseTest {
    private static ArchiveReaderUnit mArchiveReaderUnit;

    @BeforeClass
    public static void setup() {
        mArchiveReaderUnit = new ArchiveReaderUnit(mArchivePath);
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void testReadDirPerformance() throws Exception {
        final int[] stats = {
            0, // Files
            0, // Invalid files
            0, // Length
            0  // Directories
        };

        ArchiveReader archiveReader = new ArchiveReader(new ArchiveReader.IArchiveReaderCallback() {
            @Override
            public void processFileContent(ArchiveReader archiveReader1, File file) {
                String content;
                try {
                    content = ArchiveReader.readRawFile(file);
                } catch (IOException e) {
                    stats[1]++;
                    return;
                }
                stats[0]++;
                stats[2] += content.length();

                if (stats[0] == 100000) archiveReader1.cancelOperation();
            }

            @Override
            public void processDirChanged(File dir) {
                stats[3]++;
            }
        });

        archiveReader.readDir(new File(mArchiveReaderUnit.getPath()));
        System.out.println(String.format("successfully read %d chars from %d files and %d directories. %d invalid files.", stats[2], stats[0], stats[3], stats[1]));
    }
}
