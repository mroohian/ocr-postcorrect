package de.iisys.ocr.test.unit.possequence;

import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.ocr.corpa.TokenCorpa;
import de.iisys.ocr.possequence.POSSequenceTrigramTransducer;
import de.iisys.ocr.possequence.feature.HasPropertyFeature;
import de.iisys.ocr.possequence.feature.MixedTrigramFeature;
import de.iisys.ocr.possequence.feature.NextHasPropertyFeature;
import de.iisys.ocr.possequence.feature.POSTrigramFeature;
import de.iisys.ocr.possequence.feature.core.IFeature;
import de.iisys.ocr.possequence.property.*;
import de.iisys.ocr.possequence.property.core.IProperty;

import java.io.IOException;
import java.util.*;

/**
 * TestModel
 * Created by reza on 26.01.15.
 */
public class TestModel extends POSSequenceTrigramTransducer {
     private TestModel(TokenCorpa corpa, double maxDist, List<IProperty> properties, Map<IFeature, Double> featuresMap) {
        super(corpa, maxDist, properties, featuresMap);
    }

    private static List<IProperty> getProperties(Corpa corpa) {
        List<IProperty> properties = new ArrayList<IProperty>();
        properties.add(new WordIdProperty(corpa));
        properties.add(new CapitalizeProperty());
        properties.add(new ArticleProperty());
        properties.add(new PunctuationProperty());
        properties.add(new DigitProperty());
        return properties;
    }

    private static Map<IFeature, Double> getFeatures(TokenCorpa corpa, List<IProperty> properties) throws IOException, ClassNotFoundException {
        // Trigram files
        String file = "tmp/generator";
        String mixedTrigramsFile = file + ".mtg";
        String posTrigramsFile = file + ".ptg";

        SortedMap<IFeature, Double> featuresMap = new TreeMap<IFeature, Double>(new Comparator<IFeature>() {
            @Override
            public int compare(IFeature o1, IFeature o2) {
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
        });
        //TODO: fix
        for (short posId : corpa.getAllPosIDs()) {
            featuresMap.put(new HasPropertyFeature(POSSequenceTrigramTransducer.getPropertyIndex(properties,
                    ArticleProperty.class), posId), 1.0);
        }
        featuresMap.put(new HasPropertyFeature(POSSequenceTrigramTransducer.getPropertyIndex(properties,
                ArticleProperty.class), corpa.getPOSID("DT")), 1.0);
        featuresMap.put(new NextHasPropertyFeature(POSSequenceTrigramTransducer.getPropertyIndex(properties,
                PunctuationProperty.class), corpa.getPOSID("NN")), 1.0);
        featuresMap.put(POSTrigramFeature.loadFeature(posTrigramsFile), 1.0);
        featuresMap.put(MixedTrigramFeature.loadFeature(mixedTrigramsFile, corpa,
                POSSequenceTrigramTransducer.getPropertyIndex(properties, WordIdProperty.class)), 1.0);

        return featuresMap;
    }

    public static POSSequenceTrigramTransducer makeModel(TokenCorpa corpa, double[] weights, double L) throws IOException, ClassNotFoundException {
        List<IProperty> properties = getProperties(corpa);
        Map<IFeature, Double> features = getFeatures(corpa, properties);
        TestModel model = new TestModel(corpa, L, properties, features);
        if (weights != null) {
            for (int i = 0; i < model.getFeatureCount(); i++) {
                model.setFeatureWeights(i, weights[i]);
            }
        }
        return model;
    }
}
