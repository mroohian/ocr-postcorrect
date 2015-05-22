package de.iisys.ocr;

import de.iisys.ocr.archive.ArchiveReader;
import de.iisys.ocr.graph.GraphDB;
import de.iisys.ocr.graph.LocalGraphDB;
import de.iisys.ocr.graph.RemoteGraphDB;
import de.iisys.ocr.tokenizer.BasicGermanNormalizer;
import de.iisys.ocr.tokenizer.INormalizer;
import de.iisys.ocr.tokenizer.StanfordTokenizer;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;

/**
 * Archive
 * de.iisys.ocr
 * Created by reza on 25.07.14.
 */
public class App {
    private final String mImagePath; // = "/home/reza/workspace/Archive/Modules/Tess4J/eurotext.png";
    private final String mTesseractDataPath; // = "/home/reza/workspace/Archive/Modules/Tess4J/tessdata";

    // TODO: should be dynamically read from parameters or config files
    private final String mLocalPassword = "admin";
    private final String mLocalUser = "admin";
    private final String mUser = "reza";
    private final String mPassword = "sec12345";
    //private final String mServer = "127.0.0.1";
    private final String mServer = "172.16.50.110";

    private final String mCorpusFile; // = "/home/reza/Desktop/corpa/derewo-v-100000t-2009-04-30-0.1.utf8";

    private final String mBasePath;
    private final String mDbName;

    public App(String baseDir, String dbName) {
        mBasePath = baseDir;
        mDbName = dbName;

        // tesseract
        mImagePath = baseDir + "Modules/tess4j/eurotext.png";
        mTesseractDataPath = baseDir + "Modules/tess4j/tessdata";

        // corpus file
        mCorpusFile = baseDir + "data/corpa/derewo-v-100000t-2009-04-30-0.1.utf8";
    }

    public GraphDB openLocalDb() {
        final String dbPath = mDbName.startsWith("/") ? mDbName : mBasePath + "db/" + mDbName;
        return new LocalGraphDB(dbPath, mLocalUser, mLocalPassword);
    }

    public GraphDB openRemoteDb() throws IOException {
        return new RemoteGraphDB(mServer, mDbName, mUser, mPassword);
    }

    public void testTesseract() {
        // Tesseract
        File imageFile = new File(mImagePath);
        Tesseract instance = Tesseract.getInstance();  // JNA Interface Mapping
        // Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping

        instance.setDatapath(mTesseractDataPath);

        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }

    private void generateArchive(final GraphDB graphDB, String param) {
        final ArchiveReader archiveReader = new ArchiveReader(new ArchiveReader.IArchiveReaderCallback() {
            final INormalizer normalizer = new BasicGermanNormalizer();

            @Override
            public void processFileContent(ArchiveReader archiveReader1, File file) {
                String content;
                try {
                    content = ArchiveReader.readRawFile(file);
                } catch (IOException e) {
                    // TODO: write log
                    return;
                }
                //System.err.println("file:" + file.getAbsolutePath());
                //graphDB.populateDataFollowedBy(content);
                graphDB.populateDataComplete(new StanfordTokenizer(normalizer, content));
            }

            @Override
            public void processDirChanged(File dir) {
                System.err.println("processing dir:" + dir.getAbsolutePath());
            }
        });

        try {
            // TODO: hasData is not really useful here
            if (!graphDB.hasData()) {
                String path = param != null ? mBasePath + param : mBasePath;
                archiveReader.readDir(new File(path));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            graphDB.dispose();
        }
    }

    // TODO: remove this
    /*
    public void testLevDistCorpa() {
        DeReWoCorpa corpa = new DeReWoCorpa(mCorpusFile);
        corpa.load(true);

        int cutoff = 2;
        Map<Double, List<INode>> results = corpa.findSimilarWords("datteln", cutoff);

        for (int dist = 0; dist <= cutoff * 10; dist++) {
            System.out.println("Distance: " + dist);
            for (INode node : results.get(dist / 10.0)) {
                System.out.println("\t- " + node.getWord());
            }
        }
    }

    private void testLattice(GraphDB graphDB, String input) {
        final int L = 2;
        String[] inputs = input.split(" ");
        Token[] tokens = new Token[inputs.length];

        Map<IFeature, Double> mFeaturesMap = new HashMap<IFeature, Double>();
        mFeaturesMap.put(new SpellCorrectorDistanceFeature(L), 1.5);
        mFeaturesMap.put(new SpellCorrectorRankFeature(), 1.0);
        mFeaturesMap.put(new SpellCorrectorGraphBigramFeature(graphDB), 1.0);

        for (int i = 0; i < inputs.length; i++) {
            tokens[i] = new Token(inputs[i]);
        }

        DeReWoCorpa corpa = new DeReWoCorpa(mCorpusFile);
        corpa.load(true);
        System.err.println("Corpa size: " + corpa.size());
        ITransducer transducer = new SpellCorrectorTransducer(corpa, L, mFeaturesMap);

        SpellCorrectorLattice lattice = new SpellCorrectorLattice(transducer, tokens);
        lattice.computeAlphaBeta();
        lattice.computeViterbi();

        INode[] labels = lattice.getOptimumLabelSequence();
        for (INode label: labels) System.out.print(label.getWord() + " ");
        System.out.println();

        lattice.printLattice(40);
    }*/

    /**
     * @param args refer to usage
     */
    public static void main(String[] args) {
        if (args.length != 4 && args.length != 5) {
            throw new IllegalArgumentException("Usage: archive base_dir dbMode db mode [param]");
        }

        final String baseDir = args[0].endsWith("/") ? args[0] : args[0] + "/";
        final String dbMode = args[1];
        final String dbName = args[2];
        final String mode = args[3];
        final String param = args.length == 5 ? args[4] : null;

        if (!new File(baseDir).exists()) {
            throw new IllegalArgumentException("Invalid path: " + baseDir);
        }

        App app = new App(baseDir, dbName);

        final GraphDB graphDB;
        try {
            if (dbMode.equals("local")) {
                graphDB = app.openLocalDb();
            } else if (dbMode.equals("remote")) {
                graphDB = app.openRemoteDb();
            } else {
                throw new IllegalArgumentException("invalid dbMode value provided: " + dbMode);
            }
        } catch (IOException e) {
            // TODO: do something more meaningful here
            e.printStackTrace();
            return;
        }

        if (mode.equals("generateArchive")) {
            app.generateArchive(graphDB, param);

        //} else if (mode.equals("test")) {
        //    app.testLattice(graphDB, "AEine Insel ist Feine in einem MeerB . J�hrige In sel.");

        } else if (mode.equals("tesseract")) {
            app.testTesseract();

        //} else if (mode.equals("levCorpa")) {
        //    app.testLevDistCorpa();

        } else {
            throw new IllegalArgumentException("Invalid mode value provided: " + mode);
        }
    }
}
