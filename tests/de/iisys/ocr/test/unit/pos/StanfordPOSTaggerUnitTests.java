package de.iisys.ocr.test.unit.pos;

import de.iisys.ocr.pos.*;
import de.iisys.ocr.test.core.BaseTest;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;

import java.util.List;

/**
 * StanfordPOSTaggerUnitTests
 * de.iisys.ocr.test.unit.pos
 * Created by reza on 26.11.14.
 */
public class StanfordPOSTaggerUnitTests extends BaseTest {
    private final String englishContent = "This is a test. This the another test. This is the final test.";
    private final String germanContent = "Dies ist ein Test. Dies ist ein weiterer Test. Dies ist der letzte Test.";

    @Test
    public void testExtractSentencesEnglish() {
        IPOSTagger tagger = new StanfordEnglishPOSTagger(mStanfordPOSTaggerModelPath);
        List<ISentence> sentences = tagger.extractSentences(englishContent);

        assert sentences.size() == 3;
    }

    @Test
    public void testExtractSentencesGermanDewac() {
        IPOSTagger tagger = new StanfordGermanDewacPOSTagger(mStanfordPOSTaggerModelPath);
        List<ISentence> sentences = tagger.extractSentences(germanContent);

        assert sentences.size() == 3;
    }

    @Test
    public void testTagSentenceEnglish() {
        IPOSTagger tagger = new StanfordEnglishPOSTagger(mStanfordPOSTaggerModelPath);
        List<ISentence> sentences = tagger.extractSentences(englishContent);

        StanfordPOSTagger.StanfordSentence sentence = (StanfordPOSTagger.StanfordSentence) sentences.get(0);
        tagger.tagSentence(sentence);

        assert sentence.get(0).get(PartOfSpeechAnnotation.class).equals("DT");
    }

    @Test
    public void testTagSentenceGermanDewac() {
        IPOSTagger tagger = new StanfordGermanDewacPOSTagger(mStanfordPOSTaggerModelPath);
        List<ISentence> sentences = tagger.extractSentences(germanContent);

        StanfordPOSTagger.StanfordSentence sentence = (StanfordPOSTagger.StanfordSentence) sentences.get(0);

        tagger.tagSentence(sentence);

        assert sentence.get(0).get(PartOfSpeechAnnotation.class).equals("PDS");
    }

    @Test
    public void testTagSentenceEnglishPrint() {
        IPOSTagger tagger = new StanfordEnglishPOSTagger(mStanfordPOSTaggerModelPath);
        List<ISentence> sentences = tagger.extractSentences("This aged portion of society were distinguished from others.");

        StanfordPOSTagger.StanfordSentence sentence = (StanfordPOSTagger.StanfordSentence) sentences.get(0);
        tagger.tagSentence(sentence);

        for (int j = 0; j < sentence.size(); j++) {
            CoreLabel label = sentence.get(j);
            System.out.println(label.word() + "/" + label.get(PartOfSpeechAnnotation.class));
        }
    }
}
