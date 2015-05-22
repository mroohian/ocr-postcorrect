package de.iisys.ocr.test.unit.trainingset;

import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.ocr.archive.ArchiveReader;
import de.iisys.ocr.corpa.WordPOSCorpa;
import de.iisys.ocr.pos.StanfordEnglishPOSTagger;
import de.iisys.ocr.test.core.BaseTest;
import de.iisys.ocr.trainingset.TrainingSetGenerator;
import org.junit.Test;

import java.io.*;

/**
 * TrainingSetGeneratorUnitTest
 * Created by reza on 10.01.15.
 */
public class TrainingSetGeneratorUnitTest extends BaseTest {
    @Test
    public void testCreateGenerator() throws InterruptedException, IOException {
        final TrainingSetGenerator generator = new TrainingSetGenerator(new StanfordEnglishPOSTagger(BaseTest.mStanfordPOSTaggerModelPath));
        ArchiveReader archiveReader = new ArchiveReader(new ArchiveReader.IArchiveReaderCallback() {
            @Override
            public void processFileContent(ArchiveReader archiveReader1, File file) {
                assert file != null;
                generator.executeFile(file);
            }

            @Override
            public void processDirChanged(File dir) {
            }
        });

        //archiveReader.readDir(new File(mArchivePath));
        //archiveReader.readDir(new File(mArchivePath + "/en/health/"));
        //archiveReader.readDir(new File(mArchivePath + "/en/society/"));
        archiveReader.readDir(new File(mArchivePath + "/en/computer/"));
        //archiveReader.readDir(new File(mArchivePath + "/en/computer/InternetTheTimes"));
        generator.awaitTermination();
        generator.write("tmp/generator");
        //generator.writeTraining("tmp/train/generator.train");
        //generator.printStatistics();
        //generator.write("tmp/en-health-society-computer.data");
    }

    @Test
    public void testLoadGenerator() throws IOException, ClassNotFoundException {
        final TrainingSetGenerator generator;
        generator = new TrainingSetGenerator("tmp/generator");
        //generator = new TrainingSetGenerator("tmp/en-health.data");

        generator.printStatistics();
    }

    @Test
    public void testLoadCorpa() throws IOException, ClassNotFoundException {
        Corpa corpa = new WordPOSCorpa("tmp/generator.pab", "tmp/generator.wab", "tmp/generator.wpm");

        corpa.load();
    }
}