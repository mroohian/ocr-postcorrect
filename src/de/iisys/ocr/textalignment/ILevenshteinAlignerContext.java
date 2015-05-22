package de.iisys.ocr.textalignment;

/**
 * ILevenshteinAlignerContext
 * de.iisys.ocr.test.unit.textalignment
 * Created by reza on 25.11.14.
 */
public interface ILevenshteinAlignerContext {
    int sub(char c1, char c2);
    int ins(char c);
    int del(char c);

    String normalize(String input);
    boolean isDelimiter(char c);
}
