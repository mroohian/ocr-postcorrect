package de.iisys.ocr.ocr;

/**
 * OCRCorpusSentence
 * Created by reza on 04.02.15.
 */
public class OCRCorpusSentence {
    private final String[] words;
    private final String[] ocrWords;
    private final String[] posTags;
    private final boolean[] correct;

    public OCRCorpusSentence(String[] words, String[] ocrWords, String[] posTags, boolean[] correct) {
        this.words = words;
        this.ocrWords = ocrWords;
        this.posTags = posTags;
        this.correct = correct;
    }

    public String[] getWords() {
        return words;
    }

    public String[] getOcrWords() {
        return ocrWords;
    }

    public String[] getPosTags() {
        return posTags;
    }

    public boolean[] getCorrect() {
        return correct;
    }
}
