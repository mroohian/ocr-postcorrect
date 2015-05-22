package de.iisys.ocr.tokenizer;

/**
 * ITokenizer
 * de.iisys.ocr.tokenizer
 * Created by reza on 22.08.14.
 */
public interface ITokenizer {
    public boolean hasNext();
    public String next();
}
