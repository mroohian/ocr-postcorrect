package de.iisys.ocr.types;

import java.util.HashMap;
import java.util.Map;

/**
 * SparseVector
 * Created by reza on 25.01.15.
 */
public class SparseVector {
    private final int N;             // length
    private Map<Integer, Integer> st;  // the vector, represented by index-value pairs

    // initialize the all 0s vector of length N
    public SparseVector(int N) {
        this.N  = N;
        this.st = new HashMap<Integer, Integer>();
    }

    // put st[i] = value
    public void put(int i, int value) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (value == 0.0) st.remove(i);
        else              st.put(i, value);
    }

    // return st[i]
    public int get(int i) {
        if (i < 0 || i >= N) throw new RuntimeException("Illegal index");
        if (st.containsKey(i)) return st.get(i);
        else                return 0;
    }

    // return the number of nonzero entries
    public int nnz() {
        return st.size();
    }

    // return the size of the vector
    public int size() {
        return N;
    }

    // return the dot product of this vector a with b
    public int dot(SparseVector b) {
        SparseVector a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        int sum = 0;

        // iterate over the vector with the fewest nonzeros
        if (a.st.size() <= b.st.size()) {
            for (int i : a.st.keySet())
                if (b.st.containsKey(i)) sum += a.get(i) * b.get(i);
        }
        else  {
            for (int i : b.st.keySet())
                if (a.st.containsKey(i)) sum += a.get(i) * b.get(i);
        }
        return sum;
    }

    // return the 2-norm
    public double norm() {
        SparseVector a = this;
        return Math.sqrt(a.dot(a));
    }

    // return alpha * a
    public SparseVector scale(int alpha) {
        SparseVector a = this;
        SparseVector c = new SparseVector(N);
        for (int i : a.st.keySet()) c.put(i, alpha * a.get(i));
        return c;
    }

    // return a + b
    public SparseVector plus(SparseVector b) {
        SparseVector a = this;
        if (a.N != b.N) throw new RuntimeException("Vector lengths disagree");
        SparseVector c = new SparseVector(N);
        for (int i : a.st.keySet()) c.put(i, a.get(i));                // c = a
        for (int i : b.st.keySet()) c.put(i, b.get(i) + c.get(i));     // c = c + b
        return c;
    }

    // return a string representation
    public String toString() {
        String s = "";
        for (int i : st.keySet()) {
            s += "(" + i + ", " + st.get(i) + ") ";
        }
        return s;
    }
}
