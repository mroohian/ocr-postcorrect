package de.iisys.ocr.app;

import de.iisys.ocr.config.Options;
import de.iisys.ocr.corpa.DeReWoCorpa;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.corpa.WordPOSCorpa;
import de.iisys.ocr.ocr.IOCR;
import de.iisys.ocr.ocr.TesseractOCR;
import de.iisys.ocr.spellcorrector.trigram.SpellCorrectorTrigramTransducer;
import de.iisys.ocr.spellcorrector.trigram.features.SpellCorrectorDistanceTrigramFeature;
import de.iisys.ocr.transducer.features.IFeature;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.types.Token;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * CorrectorApp
 * de.iisys.ocr.app
 * Created by reza on 27.11.14.
 */
public class CorrectorApp extends App {
    private TokenCorpa corpa;

    public CorrectorApp(Options options) {
        super(options);
    }

    @Override
    protected void run() {
        // Configs
        // - Tesseract OCR
        final String tesseractDataPath = getConfig("TesseractDataPath");
        final String tesseractLanguage = getConfig("TesseractLanguage");

        System.out.println("Hi, this is corrector app!");

        if (hasOption("a")) {
            // React to option -a
            System.out.println("a is set");
        }

        if (hasOption("log")) {
            // React to option -log
            String logfile = getOption("log");
            System.out.println("logfile " + logfile);
        }

        //TODO: get from parameters
        String inputImage = "test.png";

        File inputImageFile = new File(inputImage);

        // Configure the tesseract OCR
        IOCR ocr = new TesseractOCR(tesseractDataPath);
        ocr.setLanguage(tesseractLanguage);

        // Run the ocr on the input image
        String ocrNoisyOutput = ocr.runOCR(inputImageFile);


        // Generate candidate list and transducer model
        final double L = 2;
        final String corpusFile = System.getProperty("user.dir") + "/data/corpa/derewo-v-100000t-2009-04-30-0.1.utf8";
        final Map<IFeature, Double> mFeaturesMap = featureMap(L);

        // DeReWo corpa
        //corpa = new DeReWoCorpa(corpusFile);

        // Corpus generated from digital archive
        // TODO: get path from config file
        try {
            corpa = new WordPOSCorpa("tmp/generator.pab", "tmp/generator.wab", "tmp/generator.wpm");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        new SpellCorrectorTrigramTransducer(corpa, L, mFeaturesMap);
    }

    protected Map<IFeature, Double> featureMap(final double editDist) {
        IFeature sampleFeature1 = new IFeature() {
            @Override
            public String getName() { return "sampleFeature1"; }

            @Override
            public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (prevNode2.getNode().equals(Token.START_NODE) &&
                        prevNode1.getNode().equals(Token.START_NODE) &&
                        corpa.getWordById(node.getNode().getWordId()).equals("Eine") ? 1 : 0);
            }
        };

        IFeature sampleFeature2 = new IFeature() {
            @Override
            public String getName() { return "sampleFeature2"; }

            @Override
            public double computeValue(Object[] extras, String[] inputs, int j, ILatticeNode prevNode2, ILatticeNode prevNode1, ILatticeNode node) {
                return (prevNode2.getNode().equals(Token.START_NODE) &&
                        corpa.getWordById(prevNode1.getNode().getWordId()).equals("Eine") &&
                        corpa.getWordById(node.getNode().getWordId()).equals("Aline") ? 1 : 0);
            }
        };

        HashMap<IFeature, Double> featuresMap = new HashMap<IFeature, Double>();
        featuresMap.put(new SpellCorrectorDistanceTrigramFeature(editDist), 2.0);
        featuresMap.put(sampleFeature1, 1.0);
        featuresMap.put(sampleFeature2, 1.0);
        return featuresMap;
    }
}
