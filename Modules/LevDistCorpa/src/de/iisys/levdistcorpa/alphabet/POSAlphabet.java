package de.iisys.levdistcorpa.alphabet;

import de.iisys.levdistcorpa.types.EndNode;
import de.iisys.levdistcorpa.types.StartNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * POSAlphabet
* Created by reza on 10.01.15.
*/
public class POSAlphabet extends HashMap<String, Short> {
    private Map<Short, String> reverseMap;
    private short idCounter;

    public POSAlphabet() {
        idCounter = 0;
        reverseMap = new HashMap<Short, String>();
        put(StartNode.ID, StartNode.SHORT_INDEX);
        reverseMap.put(StartNode.SHORT_INDEX, StartNode.ID);
        put(EndNode.ID, EndNode.SHORT_INDEX);
        reverseMap.put(EndNode.SHORT_INDEX, EndNode.ID);
    }

    public POSAlphabet(String posAlphabetFile) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(posAlphabetFile));
        read(ois);
        ois.close();
    }

    public synchronized short lookup(String pos, boolean add) throws NullPointerException {
        if (!containsKey(pos) && add) {
            put(pos, ++idCounter);
            reverseMap.put(idCounter, pos);
        }

        return get(pos);
    }

    public String lookupValue(short posID) throws NullPointerException {
        return reverseMap.get(posID);
    }

    public short lookup(String pos) throws NullPointerException {
        return lookup(pos, false);
    }

    public short add(String pos) {
        return lookup(pos, true);
    }

    public void write(ObjectOutputStream oos) throws IOException {
        oos.writeShort(idCounter);
        oos.writeObject(reverseMap);
    }

    public void read(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        idCounter = ois.readShort();
        reverseMap = (Map<Short, String>) ois.readObject();

        for (short id : reverseMap.keySet()) {
            put(reverseMap.get(id), id);
        }
    }

    public void print() {
        System.out.println("Alphabet POS: " + size());

        int n = 0;
        for (String pos : keySet()) {
            System.out.print(pos + ", ");

            if (n++ > 100) break;
        }
        System.out.println();
    }
}
