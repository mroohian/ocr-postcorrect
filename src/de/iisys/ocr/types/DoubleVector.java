package de.iisys.ocr.types;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * DoubleVector
 * de.iisys.ocr.types
 * Created by reza on 16.09.14.
 */
public class DoubleVector {
    private final double[] vector;

    public DoubleVector(int length) {
        vector = new double[length];
    }

    public DoubleVector(List<Double> values) {
        vector = new double[values.size()];
        for (int i = 0; i < values.size(); i++) vector[i] = values.get(i);
    }

    public DoubleVector(Collection<Double> values) {
        vector = new double[values.size()];
        Iterator<Double> iterator = values.iterator();
        for (int i = 0; i < values.size(); i++) vector[i] = iterator.next();
    }

    public DoubleVector(double[] parameters) {
        vector = java.util.Arrays.copyOf(parameters, parameters.length);
    }

    public double[] toArray() {
        return vector;
    }

    public double[] toArrayCopy() {
        return java.util.Arrays.copyOf(vector, vector.length);
    }

    public int size() {
        return vector == null ? 0 : vector.length;
    }

    public double get(int index) {
        assert index >= 0 && index < size();
        return vector[index];
    }

    public void set(int index, double value) {
        assert index >= 0 && index < size();
        vector[index] = value;
    }

    public void set(double[] parameters) {
        assert vector.length == parameters.length;
        System.arraycopy(parameters, 0, vector, 0, vector.length);
    }

    public DoubleVector sum(DoubleVector input) {
        assert size() == input.size();

        DoubleVector result = new DoubleVector(size());
        for (int i = 0; i < size(); i++) result.set(i, get(i) + input.get(i));
        return result;
    }

    public DoubleVector subtract(DoubleVector input) {
        assert size() == input.size();

        DoubleVector result = new DoubleVector(size());
        for (int i = 0; i < size(); i++) result.set(i, get(i) - input.get(i));
        return result;
    }

    public DoubleVector product(DoubleVector input) {
        assert size() == input.size();

        DoubleVector result = new DoubleVector(size());
        for (int i = 0; i < size(); i++) result.set(i, get(i) * input.get(i));
        return result;
    }

    public DoubleVector divide(DoubleVector input) {
        assert size() == input.size();

        DoubleVector result = new DoubleVector(size());
        for (int i = 0; i < size(); i++) result.set(i, get(i) / input.get(i));
        return result;
    }

    public DoubleVector sum(double value) {
        DoubleVector result = new DoubleVector(size());
        for (int i = 0; i < size(); i++) result.set(i, get(i) + value);
        return result;
    }

    public DoubleVector subtract(double value) {
        DoubleVector result = new DoubleVector(size());
        for (int i = 0; i < size(); i++) result.set(i, get(i) - value);
        return result;
    }

    public DoubleVector product(double value) {
        DoubleVector result = new DoubleVector(size());
        for (int i = 0; i < size(); i++) result.set(i, get(i) * value);
        return result;
    }

    public DoubleVector divide(double value) {
        assert value != 0.0;
        DoubleVector result = new DoubleVector(size());
        for (int i = 0; i < size(); i++) result.set(i, get(i) / value);
        return result;
    }

    public double vectorSum() {
        double sumVal = 0.0;
        for (int i = 0; i < size(); i++) sumVal += get(i);
        return sumVal;
    }

    public double vectorLength() {
        double sumVal = 0.0;
        for (int i = 0; i < size(); i++) sumVal += get(i) * get(i);
        return Math.sqrt(sumVal);
    }
}