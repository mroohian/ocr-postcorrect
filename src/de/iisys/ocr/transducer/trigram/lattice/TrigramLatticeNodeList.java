package de.iisys.ocr.transducer.trigram.lattice;

import de.iisys.ocr.types.Token;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * TrigramLatticeNodeList
 * de.iisys.ocr.transducer.trigram.lattice
 * Created by reza on 15.10.14.
 */
public class TrigramLatticeNodeList extends ArrayList<ITrigramLatticeNode> implements ITrigramLatticeNodeList {
    private final ITrigramLatticeNode latticeNode;
    private final int sequencePosition;
    protected final boolean preceding;
    protected final boolean ending;

    public static final ITrigramLatticeNode START_LATTICE_NODE = new TrigramLatticeNode(Token.START_NODE, null, true) {
        public String getDetails() { return "⊥"; }
    };
    public static final ITrigramLatticeNode STOP_LATTICE_NODE = new TrigramLatticeNode(Token.STOP_NODE, null, true) {
        public String getDetails() { return "⊤"; }
    };

    public TrigramLatticeNodeList(int sequencePosition, int sequenceLength) {
        super();
        this.sequencePosition = sequencePosition;
        this.preceding = (sequencePosition < 0);
        this.ending = (sequencePosition >= sequenceLength);

        this.latticeNode = null;
    }

    public TrigramLatticeNodeList(int sequencePosition, int sequenceLength, ITrigramLatticeNode latticeNode) {
        super();
        this.sequencePosition = sequencePosition;
        this.preceding = (sequencePosition < 0);
        this.ending = (sequencePosition >= sequenceLength);

        this.latticeNode = latticeNode;
    }

    @Override
    public Iterator<ITrigramLatticeNode> iterator() {
        final int length = (ending || preceding ? 1 : this.size());

        return new Iterator<ITrigramLatticeNode>() {
            private int pos = 0;

            @Override
            public boolean hasNext() {
                return pos < length;
            }

            @Override
            public ITrigramLatticeNode next() {
                final ITrigramLatticeNode node;

                if (pos == 0) {
                    if (preceding) {
                        node = (latticeNode != null ? latticeNode : TrigramLatticeNodeList.START_LATTICE_NODE);
                    } else if (ending) {
                        node = (latticeNode != null ? latticeNode : TrigramLatticeNodeList.STOP_LATTICE_NODE);
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
