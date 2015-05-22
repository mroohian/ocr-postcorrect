package de.iisys.ocr.spellcorrector.trigram.lattice;

import de.iisys.levdistcorpa.types.INode;
import de.iisys.ocr.transducer.lattice.ILatticeNode;
import de.iisys.ocr.transducer.lattice.ILatticeNodeList;
import de.iisys.ocr.transducer.trigram.ITrigramTransducer;
import de.iisys.ocr.transducer.trigram.lattice.ITrigramLatticeNodeList;
import de.iisys.ocr.transducer.trigram.lattice.TrigramLattice;
import de.iisys.ocr.utils.TextUtils;

/**
 * SpellCorrectorTrigramLattice
 * de.iisys.ocr.spellcorrector.trigram.lattice
 * Created by reza on 15.10.14.
 */
public class SpellCorrectorTrigramLattice extends TrigramLattice {
    protected int mMaxTokenCandidate;

    public SpellCorrectorTrigramLattice(ITrigramTransducer transducer, String[] input) {
        super(transducer, input, null, null);
    }

    public SpellCorrectorTrigramLattice(ITrigramTransducer transducer, String[] input, Object[] extras) {
        super(transducer, input, extras, null);
    }

    public SpellCorrectorTrigramLattice(ITrigramTransducer transducer, String[] input, INode[] output) {
        super(transducer, input, null, output);
    }

    public SpellCorrectorTrigramLattice(ITrigramTransducer transducer, String[] input, Object[] extras, INode[] output) {
        super(transducer, input, extras, output);
    }

    @Override
    protected void fillLattice() {
        // fill the lattice with candidate lists and compute length of longest column
        int maxTokenCandidate = 0;
        ITrigramLatticeNodeList prevList = startList1;
        for (int j = 0; j < mLength; j++) {
            ITrigramLatticeNodeList candidateList = ((ITrigramTransducer)getTransducer()).buildCandidateList(j, mLength, mInput[j], prevList);
            assert  (candidateList.size() != 0);
            latticeNodes[j] = candidateList;
            prevList = candidateList;
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
