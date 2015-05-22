package de.iisys.ocr.ocr;

import de.iisys.ocr.archive.ArchiveReader;
import de.iisys.ocr.pos.*;
import de.iisys.ocrcorpa.TextRenderer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * OCRCorpusGenerator
 * de.iisys.ocr.ocr
 * Created by reza on 25.11.14.
 */
public class OCRCorpusGenerator {
    private final IOCR ocr;
    private final IPOSTagger posTagger;
    private final TextRenderer textRenderer;

    public OCRCorpusGenerator(IOCR ocr, IPOSTagger posTagger) {
        this.ocr = ocr;
        this.posTagger = posTagger;

        //textRenderer = new TextRenderer("Arial", 26);
        textRenderer = new TextRenderer("Arial", 20);
        //textRenderer = new TextRenderer("Arial", 16);
        textRenderer.setNoiseLevel(20);
        textRenderer.enableForgroundNoise();
        textRenderer.setForgroundNoiseLevel(15);
    }

    public void generateCorpusFromArchive(String archiveDirectory, String outputDirectory) {
        String basePath = archiveDirectory;
        final int basePathLength = basePath.length() + (basePath.endsWith("/") ? 0 : 1);

        final String outputPath = outputDirectory + (outputDirectory.endsWith("/") ? "" : "/");

        final CorpusFileGeneratorPoolExecutor poolExecutor = new CorpusFileGeneratorPoolExecutor();

        // TODO: make this part multi threaded
        ArchiveReader archiveReader = new ArchiveReader(new ArchiveReader.IArchiveReaderCallback() {
            @Override
            public void processFileContent(ArchiveReader archiveReader1, File file) {
                String outputFilePath = outputPath + file.getAbsolutePath().substring(basePathLength);
                outputFilePath = outputFilePath.substring(0,outputFilePath.length()-4) + ".ocr.xml";

                OCRCorpusFileGeneratorRunnable generatorRunnable;
                try {
                    generatorRunnable = new OCRCorpusFileGeneratorRunnable(outputFilePath, file.getAbsolutePath(), ocr, posTagger, textRenderer);
                } catch (IOException e) {
                    // TODO: write log
                    return;
                }

                poolExecutor.execute(generatorRunnable);

                // Just one file
                // TODO: remove
                //archiveReader1.cancelOperation();
            }

            @Override
            public void processDirChanged(File dir) {
                // Not used anymore...
                // folders will be generated as needed.
                /*if (dir.getAbsolutePath().length() < basePathLength) {
                    File outDir = new File(outputPath);
                    if (!outDir.exists()) {
                        outDir.mkdirs();
                    }
                    return;
                }
                String outputDirPath = outputPath + dir.getAbsolutePath().substring(basePathLength);
                //System.out.println("DIR: " + outputDirPath);
                File destDir = new File(outputDirPath);

                if (!destDir.exists()) destDir.mkdirs();*/
            }
        });

        archiveReader.readDir(new File(archiveDirectory));
    }

    private class CorpusFileGeneratorPoolExecutor extends ThreadPoolExecutor {
        private static final int corePoolSize = 32;
        private static final int maximumPoolSize = 32;
        private static final long keepAliveTime = 60; // 60 s

        public CorpusFileGeneratorPoolExecutor() {
            super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }
}
