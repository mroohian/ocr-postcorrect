package de.iisys.ocr.pos;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * StanfordEnglishPOSTagger
 * de.iisys.ocr.pos
 * Created by reza on 26.11.14.
 */
public class StanfordEnglishPOSTagger extends StanfordPOSTagger{
    public StanfordEnglishPOSTagger(String modelsPath) {
        super(modelsPath);
    }

    @Override
    public MaxentTagger loadTagger() {
        return new MaxentTagger(getModelsPath() + "english-caseless-left3words-distsim.tagger");
    }
}
