package de.iisys.ocr.pos;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * StanfordGermanDewacPOSTagger
 * de.iisys.ocr.pos
 * Created by reza on 26.11.14.
 */
public class StanfordGermanDewacPOSTagger extends StanfordPOSTagger {
    public StanfordGermanDewacPOSTagger(String modelsPath) {
        super(modelsPath);
    }

    @Override
    public MaxentTagger loadTagger() {
        return new MaxentTagger(getModelsPath() + "german-dewac.tagger");
    }
}
