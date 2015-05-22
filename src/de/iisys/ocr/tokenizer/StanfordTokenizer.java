package de.iisys.ocr.tokenizer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

import java.io.StringReader;

/**
 * StanfordTokenizer
 * de.iisys.ocr.tokenizer
 * Created by reza on 22.08.14.
 */
public class StanfordTokenizer implements ITokenizer {

    private final PTBTokenizer<CoreLabel> mTokenizer;
    private final INormalizer mNormalizer;

    public StanfordTokenizer(String input) {
        mNormalizer = new IdentityNormalizer();
        mTokenizer = new PTBTokenizer<CoreLabel>(new StringReader(mNormalizer.removeBadChars(input)), new CoreLabelTokenFactory(), "");
    }

    public StanfordTokenizer(INormalizer normalizer, String input) {
        mNormalizer = normalizer;
        mTokenizer = new PTBTokenizer<CoreLabel>(new StringReader(mNormalizer.removeBadChars(input)), new CoreLabelTokenFactory(), "");
    }

    @Override
    public boolean hasNext() {
        return mTokenizer.hasNext();
    }

    @Override
    public String next() {
        CoreLabel label = mTokenizer.next();
        return mNormalizer.normalize(label.value());
    }
}
