package de.iisys.ocr.textalignment;

/**
 * de.iisys.ocr.textalignment
 * Created by reza on 25.11.14.
 */
public class DefaultLevenshteinAlignerContext implements ILevenshteinAlignerContext {
    public int sub(char c1, char c2) {
        if ((c1 == ' ' || c2 == ' ') && (c1 != c2)) return 2;
        return 1;
    }

    public int ins(char c) {
        return 1;
    }

    public int del(char c) {
        return 1;
    }

    public String normalize(String input) {
        return input.replace("\n", "");
    }

    public boolean isDelimiter(char c) {
        return (c == ' ');
    }
}
