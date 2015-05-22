package de.iisys.ocr.textalignment;

/**
* de.iisys.ocr.textalignment
* Created by reza on 25.11.14.
*/
public class LevenshteinTextAlignerResult<X, Y> {
    public final X input;
    public final Y output;
    public LevenshteinTextAlignerResult(X input, Y output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public String toString() {
        return input.toString() + "/" + output.toString();
    }
}
