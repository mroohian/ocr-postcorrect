package de.iisys.ocr.possequence.property;

import de.iisys.ocr.possequence.property.core.IProperty;

import java.util.List;

/**
 * CapitalizeProperty
 * Created by reza on 25.01.15.
 */
public class CapitalizeProperty implements IProperty {
    @Override
    public int[] getValue(List<String> input) {
        int[] result = new int[input.size()];
        for (int j = 0; j < input.size(); j++) {
            String word = input.get(j);
            if (word == null) continue;
            result[j] = (Character.isUpperCase(word.charAt(0)) ? 1 : 0);
        }

        return result;
    }
}
