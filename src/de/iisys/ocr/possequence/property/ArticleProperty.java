package de.iisys.ocr.possequence.property;

import de.iisys.ocr.possequence.property.core.IProperty;

import java.util.Arrays;
import java.util.List;

/**
 * ArticleProperty
 * Created by reza on 26.01.15.
 */
public class ArticleProperty implements IProperty {
    private List<String> dts = Arrays.asList("the", "a", "an", "The", "A", "An");

    @Override
    public int[] getValue(List<String> input) {
        int[] result = new int[input.size()];

        for (int j = 0; j < input.size(); j++) {
            String word = input.get(j);
            if (word == null) continue;
            result[j] = (dts.contains(word) ? 1 : 0);
        }

        return result;
    }
}
