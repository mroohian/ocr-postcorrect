package de.iisys.ocr.textalignment;

import de.iisys.ocr.tokenizer.ITokenizer;

import java.util.ArrayList;

/**
 * de.iisys.ocr.textalignment
 * Created by reza on 26.11.14.
 */
public class LevenshteinTextAlignerResultSet extends ArrayList<LevenshteinTextAlignerResult<String, String>> {
    public ITokenizer getInputTokenizer() {
        return new ITokenizer() {
            private int pos = 0;
            @Override
            public boolean hasNext() {
                return pos < size();
            }

            @Override
            public String next() {
                return get(pos++).input;
            }
        };
    }

    public ITokenizer getOutputTokenizer() {
        return new ITokenizer() {
            private int pos = 0;
            @Override
            public boolean hasNext() {
                return pos < size();
            }

            @Override
            public String next() {
                return get(pos++).output;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("[ ");
        for (LevenshteinTextAlignerResult<String, String> result : this) {
            stringBuilder.append(result.input + ":" + result.output + " ");
        }
        stringBuilder.append("]");

        return stringBuilder.toString();
    }
}
