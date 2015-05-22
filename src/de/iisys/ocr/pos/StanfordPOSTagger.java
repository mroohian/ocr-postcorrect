package de.iisys.ocr.pos;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * StanfordPOSTagger
 * de.iisys.ocr.pos
 * Created by reza on 10.11.14.
 */
public abstract class StanfordPOSTagger implements IPOSTagger {

    public class StanfordSentence extends ArrayList<CoreLabel> implements ISentence {
        public StanfordSentence(List<CoreLabel> sentenceWords) {
            super(sentenceWords);
        }
    }

    public class StanfordTaggedSentence extends ArrayList<TaggedWord> implements ITaggedSentence {
        public StanfordTaggedSentence(ArrayList<TaggedWord> taggedWords) {
            super(taggedWords);
        }
    }

    private final String modelsPath;
    private final MaxentTagger tagger;

    public abstract MaxentTagger loadTagger();

    public StanfordPOSTagger(String modelsPath) {
        this.modelsPath = modelsPath + (modelsPath.endsWith("/")? "" : "/");
        this.tagger = loadTagger();
    }

    /*public StanfordPOSTagger() {
        this("./");
    }*/

    protected String getModelsPath() {
        return modelsPath;
    }

    @Override
    public List<ISentence> extractSentences(String content) {
        StringReader reader = new StringReader(content);
        DocumentPreprocessor docProcessor = new DocumentPreprocessor(reader);
        return extractSentences(docProcessor);
    }

    @Override
    public void tagSentence(ISentence inputSentence) {
        StanfordSentence sentence = (StanfordSentence) inputSentence;
        ArrayList<TaggedWord> result = tagger.apply(sentence);

        for (int i = 0; i < sentence.size(); i++) {
            CoreLabel token = sentence.get(i);
            token.set(PartOfSpeechAnnotation.class, result.get(i).tag());
        }
        StanfordTaggedSentence taggedSentence = new StanfordTaggedSentence(result);
    }

    public List<ISentence> extractSentences(DocumentPreprocessor docProcessor) {
        List<ISentence> sentences = new ArrayList<ISentence>();

        for (List<HasWord> sentenceWords : docProcessor) {
            List<CoreLabel> coreLabelList = Sentence.toCoreLabelList(sentenceWords);
            StanfordSentence sentence = new StanfordSentence(coreLabelList);
            sentences.add(sentence);
        }

        return sentences;
    }
}
