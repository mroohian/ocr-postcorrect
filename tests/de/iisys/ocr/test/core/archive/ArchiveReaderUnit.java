package de.iisys.ocr.test.core.archive;

import java.io.File;

/**
 * ArchiveReaderUnit
 * de.iisys.ocr.test.core.archive
 * Created by reza on 19.08.14.
 */
public class ArchiveReaderUnit {
    private final String mArchivePath;

    public ArchiveReaderUnit(String archivePath) {
        assert new File(archivePath).exists();
        mArchivePath = archivePath;
    }

    public String getPath() {
        return mArchivePath;
    }
}