package de.iisys.ocr.transducer.bigram.lattice;

import de.iisys.ocr.types.Token;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * BigramLatticeNodeList
 * de.iisys.ocr.transducer.bigram.lattice
 * Created by reza on 15.10.14.
 */
public class BigramLatticeNodeList extends ArrayList<IBigramLatticeNode> implements IBigramLatticeNodeList {
    private final IBigramLatticeNode latticeNode;
    private final int sequencePosition;
    protected final boolean preceding;
    protected final boolean ending;

    public static final IBigramLatticeNode START_LATTICE_NODE = new BigramLatticeNode(Token.START_NODE, true) {
        public String getDetails() { return "⊥"; }
    };
    public static final IBigramLatticeNode STOP_LATTICE_NODE = new BigramLatticeNode(Token.STOP_NODE, true) {
        public String getDetails() { return "⊤"; }
    };

    public BigramLatticeNodeList(int sequencePosition, int sequenceLength) {
        super();
        this.sequencePosition = sequencePosition;
        this.preceding = (sequencePosition < 0);
        this.ending = (sequencePosition >= sequenceLength);

        this.latticeNode = null;
    }

    public BigramLatticeNodeList(int sequencePosition, int sequenceLength, IBigramLatticeNode latticeNode) {
        super();
        this.sequencePosition = sequencePosition;
        this.preceding = (sequencePosition < 0);
        this.ending = (sequencePosition >= sequenceLength);

        this.latticeNode = latticeNode;
    }

    @Override
    public Iterator<IBigramLatticeNode> iterator() {
        final int length = (ending || preceding ? 1 : this.size());

        return new Iterator<IBigramLatticeNode>() {
            private int pos = 0;

            @Override
            public boolean hasNext() {
                return pos < length;
            }

            @Override
            public IBigramLatticeNode next() {
                final IBigramLatticeNode node;

                if (pos == 0) {
                    if (preceding) {
                        node = (latticeNode != null ? latticeNode : BigramLatticeNodeList.START_LATTICE_NODE);
                    } else if (ending) {
                        node = (latticeNode != null ? latticeNode : BigramLatticeNodeList.STOP_LATTICE_NODE);
                    } else {
                        node = get(0);
                    }
                } else {
                    node = get(pos);
                }

                pos++;
                return node;
            }

            @Override
            public void remove() { }
        };
    }

    @Override
    public int size() {
        if (preceding || ending) return 1;
        return super.size();
    }

    @Override
    public int getSequencePosition() {
        return sequencePosition;
    }
}
