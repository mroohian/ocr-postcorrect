package de.iisys.ocr.pos;

import java.util.List;

/**
 * IPOSTagger
 * de.iisys.ocr.pos
 * Created by reza on 26.11.14.
 */
public interface IPOSTagger {
    List<ISentence> extractSentences(String content);
    void tagSentence(ISentence sentence);
}
