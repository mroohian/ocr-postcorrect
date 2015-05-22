package de.iisys.levdistcorpa.trie;

import de.iisys.levdistcorpa.types.INode;

import java.util.ArrayList;
import java.util.List;

/**
 * TrieNode
 * de.iisys.levdistcorpa.trie
 * Created by reza on 28.07.14.
 */
public class TrieNode {
    public final static int MAX_WORD_SIZE = 255;

    public final static int TRIENODE_FLAG_NORMAL = 0x0001;
    public final static int TRIENODE_FLAG_LOWERCASE = 0x0002;
    public static final int TRIENODE_FLAG_PUNCTUATION = 0x0004;

    private INode mNode;

    private final char mContent;
    private boolean mMarker;
    private int mFlag;

    public TrieNode(char c) {
        mContent = c;
    }

    public void setNode(INode mNode) {
        this.mNode = mNode;
    }

    public INode getNode() {
        return mNode;
    }

    private List<TrieNode> mChildren = new ArrayList<TrieNode>();

    public char getContent() {
        return mContent;
    }

    public void setWordMarker() {
        this.mMarker = true;
    }

    public boolean isWordMarker() {
        return mMarker;
    }

    public List<TrieNode> getChildren() {
        return mChildren;
    }

    public TrieNode findChild(char c) {
        for (TrieNode tmp : mChildren) {
            if (tmp.getContent() == c) {
                return tmp;
            }
        }

        return null;
    }

    public void appendChild(TrieNode child) {
        mChildren.add(child);
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getFlag() {
        return mFlag;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean hasFlag(int flag) {
        return ((mFlag & flag) == flag);
    }
}
