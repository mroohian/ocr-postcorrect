package de.iisys.levdistcorpa.alphabet;

import de.iisys.levdistcorpa.types.EndNode;
import de.iisys.levdistcorpa.types.StartNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * WordAlphabet
 * Created by reza on 10.01.15.
 */
public class WordAlphabet extends HashMap<String, Integer> {
    private Map<Integer, String> reverseMap;
    private int idCounter;

    public WordAlphabet() {
        idCounter = 0;
        reverseMap = new HashMap<Integer, String>();
        put(StartNode.ID, StartNode.INT_INDEX);
        reverseMap.put(StartNode.INT_INDEX, StartNode.ID);
        put(EndNode.ID, EndNode.INT_INDEX);
        reverseMap.put(EndNode.INT_INDEX, EndNode.ID);
    }

    public WordAlphabet(String wordAlphabetFile) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(wordAlphabetFile));
        read(ois);
        ois.close();
    }

    public synchronized int lookup(String word, boolean add) throws NullPointerException{
        if (!containsKey(word) && add) {
            put(word, ++idCounter);
            reverseMap.put(idCounter, word);
        }

        return get(word);
    }

    public String lookupValue(int wordID) throws NullPointerException {
        return reverseMap.get(wordID);
    }

    public int lookup(String word) throws NullPointerException {
        return lookup(word, false);
    }

    public int add(String word) {
        return lookup(word, true);
    }

    public void write(ObjectOutputStream oos) throws IOException {
        oos.writeInt(idCounter);
        oos.writeObject(reverseMap);
    }

    public void read(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        idCounter = ois.readInt();
        reverseMap = (Map<Integer, String>) ois.readObject();

        for (int id : reverseMap.keySet()) {
            put(reverseMap.get(id), id);
        }
    }

    public void print() {
        System.out.println("Alphabet word: " + size());

        int n = 0;
        for (String word : keySet()) {
            System.out.print(word + ", ");

            if (n++ > 100) break;
        }
        System.out.println();
    }
}

