package de.iisys.ocr.tokenizer;

/**
 * DelimiterTokenizer
 * de.iisys.ocr.test.unit.tokenizer
 * Created by reza on 15.09.14.
 */
public class DelimiterTokenizer implements ITokenizer {
    private final String[] mParts;
    private final int mCount;
    private int mPos;

    public DelimiterTokenizer(String input) {
        mParts = input.split(" ");
        mCount = mParts.length;
        mPos = 0;
    }

    public DelimiterTokenizer(String input, String delimiter) {
        mParts = input.split(delimiter);
        mCount = mParts.length;
        mPos = 0;
    }

    @Override
    public boolean hasNext() {
        return mPos < mCount;
    }

    @Override
    public String next() {
        return mParts[mPos++];
    }
}
