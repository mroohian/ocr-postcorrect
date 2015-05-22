package de.iisys.ocr.corpa;

import de.iisys.levdistcorpa.alphabet.POSAlphabet;
import de.iisys.levdistcorpa.alphabet.WordAlphabet;
import de.iisys.levdistcorpa.corpa.Corpa;
import de.iisys.ocr.tokenizer.ITokenizer;
import de.iisys.ocr.types.Token;

import java.io.IOException;
import java.util.*;

/**
 * TokenCorpa
 * Created by reza on 11.01.15.
 */
public abstract class TokenCorpa extends Corpa {
    private final Set<Short> posTags;

    public TokenCorpa() {
        super();
        posTags = null;
    }

    public TokenCorpa(WordAlphabet wordAlphabet, POSAlphabet posAlphabet) {
        super(wordAlphabet, posAlphabet);

        this.posTags = new HashSet<Short>();
        for (short posID : getPosAlphabet().values()) {
            posTags.add(posID);
        }
    }

    public int getLength() {
        return getAllNodes().size();
    }

    public List<String> createStringList(ITokenizer tokenizer) {
        List<String> tokensList = new ArrayList<String>();
        for (String part; tokenizer.hasNext(); ) {
            tokensList.add(tokenizer.next());
        }

        return tokensList;
    }

    public String[] createStringArray(ITokenizer tokenizer) {
        List<String> tokensList = new ArrayList<String>();
        for (String part; tokenizer.hasNext(); ) {
            tokensList.add(tokenizer.next());
        }

        return tokensList.toArray(new String[tokensList.size()]);
    }

    public List<Token> createTokenList(ITokenizer tokenizer) {
        List<Token> tokensList = new ArrayList<Token>();
        for (String part; tokenizer.hasNext(); ) {
            part = tokenizer.next();
            // TODO: check if word can be added here. then change to addWord()
            tokensList.add(new Token(getWordId(part)));
        }

        return tokensList;
    }

    public Token[] createTokenArray(ITokenizer tokenizer) {
        List<Token> tokensList = new ArrayList<Token>();
        for (String part; tokenizer.hasNext(); ) {
            part = tokenizer.next();
            // TODO: check if word can be added here. then change to addWord()
            tokensList.add(new Token(getWordId(part)));
        }

        return tokensList.toArray(new Token[tokensList.size()]);
    }

    public List<Short> createPOSList(String posString) {
        String[] posStrings = posString.split(" ");
        List<Short> posIDs = new ArrayList<Short>();

        for (String pos : posStrings) {
            posIDs.add(getPOSID(pos));
        }

        return posIDs;
    }


    public Set<Short> getAllPosIDs() {
        return posTags;
    }
}
