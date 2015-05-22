package de.iisys.ocr.spellcorrector.bigram.lattice;

import de.iisys.ocr.transducer.bigram.IBigramTransducer;
import de.iisys.ocr.transducer.bigram.lattice.BigramLattice;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.lattice.ILatticeNodeList;
import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.utils.TextUtils;

/**
 * SpellCorrectorBigramLattice
 * de.iisys.ocr.spellcorrector.bigram.lattice
 * Created by reza on 04.08.14.
 */
public class SpellCorrectorBigramLattice extends BigramLattice {
    protected int mMaxTokenCandidate;

    public SpellCorrectorBigramLattice(IBigramTransducer transducer, String[] input) {
        super(transducer, input, null);
    }

    public SpellCorrectorBigramLattice(IBigramTransducer transducer, String[] input, INode[] output) {
        super(transducer, input, output);
    }

    @Override
    protected void fillLattice() {
        // fill the lattice with candidate lists and compute length of longest column
        int maxTokenCandidate = 0;
        for (int j = 0; j < mLength; j++) {
            latticeNodes[j] = ((IBigramTransducer)getTransducer()).buildCandidateList(j, mLength, mInput[j]);
            maxTokenCandidate = Math.max(latticeNodes[j].size(), maxTokenCandidate);
        }

        mMaxTokenCandidate = maxTokenCandidate;
    }

    public void printLattice(int columnLength) {
        for (int i = 0; i < mLength; i++) {
            System.out.print(TextUtils.formatColumnString(mInput[i], columnLength));
        }
        System.out.println();
        for (int i = 0; i < mLength; i++) {
            System.out.print(TextUtils.formatColumnString("======", columnLength));
        }
        System.out.println();

        for (int row = 0; row < mMaxTokenCandidate; row++) {
            for (int i = 0; i < mLength; i++) {
                ILatticeNodeList candidateSet = latticeNodes[i];
                if (row < candidateSet.size()) {
                    ILatticeNode latticeNode = candidateSet.get(row);

                    String columnValue = latticeNode.getDetails();

                    System.out.print(TextUtils.formatColumnString(columnValue, columnLength));
                } else {
                    System.out.print(TextUtils.formatColumnString("", columnLength));
                }
            }
            System.out.println();
            for (int i = 0; i < mLength; i++) {
                System.out.print(TextUtils.formatColumnString("------", columnLength));
            }
            System.out.println();
        }
    }
}
