package de.iisys.ocr.textalignment;

import java.util.*;

/**
 * de.iisys.ocr.test.other
 * Created by reza on 25.11.14.
 */
public class LevenshteinTextAligner {
    private final ILevenshteinAlignerContext levConfusion;

    public LevenshteinTextAligner(ILevenshteinAlignerContext levConfusion) {
        this.levConfusion = levConfusion;
    }

    public LevenshteinTextAlignerResultSet align(String input, String output) {
        input = levConfusion.normalize(input);
        int[][] costs = new int[input.length()+1][output.length()+1];
        for (int j = 0; j <= output.length(); j++)
            costs[0][j] = j;
        for (int i = 1; i <= input.length(); i++) {
            costs[i][0] = i;
            for (int j = 1; j <= output.length(); j++) {
                char cIn = input.charAt(i - 1);
                char cOut = output.charAt(j - 1);

                int costDel = costs[i-1][j] + levConfusion.del(cIn);
                int costIns = costs[i][j-1] + levConfusion.ins(cOut);
                int costSub = costs[i-1][j-1] + (cIn != cOut? levConfusion.sub(cIn, cOut): 0);

                costs[i][j] = Math.min(Math.min(costDel, costIns), costSub);
            }
        }

        // walk back through matrix to figure out path
        StringBuilder inputTokenBuilder = new StringBuilder();
        StringBuilder outputTokenBuilder = new StringBuilder();
        LevenshteinTextAlignerResultSet alignedTokens = new LevenshteinTextAlignerResultSet();
        for (int i = input.length(), j = output.length(); i != 0 && j != 0; ) {
            char cIn = input.charAt(i - 1);
            char cOut = output.charAt(j - 1);

            int costDel = costs[i-1][j] + levConfusion.del(cIn);
            int costIns = costs[i][j-1] + levConfusion.ins(cOut);
            int costSub = costs[i-1][j-1] + (cIn != cOut? levConfusion.sub(cIn, cOut): 0);

            if (costs[i][j] == costSub) {
                // Move diagonal and add to output
                --i;
                --j;

                if (!levConfusion.isDelimiter(cIn)) {
                    inputTokenBuilder.append(cIn);
                }
                // if a delimiter is matched
                if (!levConfusion.isDelimiter(cOut)) {
                    outputTokenBuilder.append(cOut);
                } else {
                    if (outputTokenBuilder.length() > 0 || inputTokenBuilder.length() > 0) {
                        String outputToken = outputTokenBuilder.reverse().toString();
                        String inputToken = inputTokenBuilder.reverse().toString();
                        alignedTokens.add(new LevenshteinTextAlignerResult<String, String>(inputToken, outputToken));
                        inputTokenBuilder = new StringBuilder();
                        outputTokenBuilder = new StringBuilder();
                    }
                }
            } else if (costs[i][j] == costDel) {
                // Moving horizontal
                --i;

                if (!levConfusion.isDelimiter(cIn)) {
                    inputTokenBuilder.append(cIn);
                } else {
                    if (outputTokenBuilder.length() > 0 || inputTokenBuilder.length() > 0) {
                        String outputToken = outputTokenBuilder.reverse().toString();
                        String inputToken = inputTokenBuilder.reverse().toString();
                        alignedTokens.add(new LevenshteinTextAlignerResult<String, String>(inputToken, outputToken));
                        inputTokenBuilder = new StringBuilder();
                        outputTokenBuilder = new StringBuilder();
                    }
                }
            } else if (costs[i][j] == costIns) {
                // Moving vertical
                --j;

                if (!levConfusion.isDelimiter(cOut)) {
                    outputTokenBuilder.append(cOut);
                } else {
                    if (outputTokenBuilder.length() > 0 || inputTokenBuilder.length() > 0) {
                        String outputToken = outputTokenBuilder.reverse().toString();
                        String inputToken = inputTokenBuilder.reverse().toString();
                        alignedTokens.add(new LevenshteinTextAlignerResult<String, String>(inputToken, outputToken));
                        inputTokenBuilder = new StringBuilder();
                        outputTokenBuilder = new StringBuilder();
                    }
                }
            }
        }
        if (outputTokenBuilder.length() > 0 || inputTokenBuilder.length() > 0) {
            String outputToken = outputTokenBuilder.reverse().toString();
            String inputToken = inputTokenBuilder.reverse().toString();
            alignedTokens.add(new LevenshteinTextAlignerResult<String, String>(inputToken, outputToken));
        }

        Collections.reverse(alignedTokens);
        return alignedTokens;
    }

    public static LevenshteinTextAlignerResultSet alignTexts(String input, String output) {
        LevenshteinTextAligner aligner = new LevenshteinTextAligner(new DefaultLevenshteinAlignerContext());
        return aligner.align(input, output);
    }

}
