package de.iisys.ocr.optimizer;

import java.util.LinkedList;

/**
 * LBFGSOptimizer
 * de.iisys.ocr.optimizer
 * Ported by reza on 18.09.14.
 */
/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

/**
 @author Aron Culotta <a href="mailto:culotta@cs.umass.edu">culotta@cs.umass.edu</a>
 */

/**
 Limited Memory BFGS, as described in Byrd, Nocedal, and Schnabel,
 "Representations of Quasi-Newton Matrices and Their Use in Limited
 Memory Methods"
 */
public class LBFGSOptimizer implements IOptimizer {
    boolean converged = false;
    IFunction optimizable;
    final int maxIterations = 1000;
    // xxx need a more principled stopping point
    //final double tolerance = .0001;
    private double tolerance = .0001;
    final double gradientTolerance = .001;
    final double eps = 1.0e-5;

    // The number of corrections used in BFGS update
    // ideally 3 <= m <= 7. Larger m means more cpu time, memory.
    final int m = 4;

    // Line search function
    private ILineOptimizer lineMaximizer;

    public LBFGSOptimizer (IFunction function) {
        this.optimizable = function;
        lineMaximizer = new BackTrackLineSearch (function);
    }

    public IFunction getOptimizable () { return this.optimizable; }
    public boolean isConverged () { return converged; }


    /**
     * Sets the LineOptimizer.ByGradient to use in L-BFGS optimization.
     * @param lineOpt line optimizer for L-BFGS
     */
    public void setLineOptimizer(ILineOptimizer lineOpt) {
        lineMaximizer = lineOpt;
    }

    // State of search
    // g = gradient
    // s = list of m previous "parameters" values
    // y = list of m previous "g" values
    // rho = intermediate calculation
    double [] g, oldg, direction, parameters, oldParameters;
    LinkedList s = new LinkedList();
    LinkedList y = new LinkedList();
    LinkedList rho = new LinkedList();
    double [] alpha;
    static double step = 1.0;
    int iterations;

    private IOptimizerEvaluator eval = null;

    // CPAL - added this
    public void setTolerance(double newtol) {
        this.tolerance = newtol;
    }

    public void setEvaluator (IOptimizerEvaluator eval) { this.eval = eval; }

    public int getIteration () {
        return iterations;
    }

    public boolean optimize ()
    {
        return optimize (Integer.MAX_VALUE);
    }

    public boolean optimize (int numIterations)
    {

        double initialValue = optimizable.getValue();
        System.err.println("Entering L-BFGS.optimize(). Initial Value="+initialValue);


        if(g==null) { //first time through
            System.err.println("First time through L-BFGS");
            iterations = 0;
            s = new LinkedList();
            y = new LinkedList();
            rho = new LinkedList();
            alpha = new double[m];
            for(int i=0; i<m; i++)
                alpha[i] = 0.0;

            parameters = new double[optimizable.getNumParams()];
            oldParameters = new double[optimizable.getNumParams()];
            g = new double[optimizable.getNumParams()];
            oldg = new double[optimizable.getNumParams()];
            direction = new double[optimizable.getNumParams()];

            optimizable.getParameters (parameters);
            System.arraycopy (parameters, 0, oldParameters, 0, parameters.length);

            optimizable.getValueGradient (g);
            System.arraycopy (g, 0, oldg, 0, g.length);
            System.arraycopy (g, 0, direction, 0, g.length);

            if (MatrixOps.absNormalize (direction) == 0) {
                System.err.println("L-BFGS initial gradient is zero; saying converged");
                g = null;
                converged = true;
                return true;
            }
            System.err.println("direction.2norm: " + MatrixOps.twoNorm (direction));
            MatrixOps.timesEquals(direction, 1.0 / MatrixOps.twoNorm(direction));
            // make initial jump
            System.err.println("before initial jump: \ndirection.2norm: " +
                    MatrixOps.twoNorm (direction) + " \ngradient.2norm: " +
                    MatrixOps.twoNorm (g) + "\nparameters.2norm: " +
                    MatrixOps.twoNorm(parameters));


            //TestMaximizable.testValueAndGradientInDirection (maxable, direction);
            step = lineMaximizer.optimize(direction, step);
            if (step == 0.0) {// could not step in this direction.
                // give up and say converged.
                g = null; // reset search
                step = 1.0;
                throw new RuntimeException("Line search could not step in the current direction. " +
                        "(This is not necessarily cause for alarm. Sometimes this happens close to the maximum," +
                        " where the function may be very flat.)");

                //return false;
            }
            optimizable.getParameters (parameters);
            optimizable.getValueGradient(g);
            System.err.println("after initial jump: \ndirection.2norm: " +
                    MatrixOps.twoNorm (direction) + " \ngradient.2norm: "
                    + MatrixOps.twoNorm (g));
        }

        for(int iterationCount = 0; iterationCount < numIterations;
            iterationCount++)	{
            double value = optimizable.getValue();
            System.err.println("L-BFGS iteration="+iterationCount
                    +", value="+value+" g.twoNorm: "+MatrixOps.twoNorm(g)+
                    " oldg.twoNorm: "+MatrixOps.twoNorm(oldg));
            // get difference between previous 2 gradients and parameters
            double sy = 0.0;
            double yy = 0.0;
            for (int i=0; i < oldParameters.length; i++) {
                // -inf - (-inf) = 0; inf - inf = 0
                if (Double.isInfinite(parameters[i]) &&
                        Double.isInfinite(oldParameters[i]) &&
                        (parameters[i]*oldParameters[i] > 0))
                    oldParameters[i] = 0.0;
                else
                    oldParameters[i] = parameters[i] - oldParameters[i];
                if (Double.isInfinite(g[i]) &&
                        Double.isInfinite(oldg[i]) &&
                        (g[i]*oldg[i] > 0))
                    oldg[i] = 0.0;
                else oldg[i] = g[i] - oldg[i];
                sy += oldParameters[i] * oldg[i]; 	 // si * yi
                yy += oldg[i]*oldg[i];
                direction[i] = g[i];
            }

            if ( sy > 0 ) {
                throw new RuntimeException ("sy = "+sy+" > 0" );
            }

            double gamma = sy / yy;	 // scaling factor
            if ( gamma>0 )
                throw new RuntimeException ("gamma = "+gamma+" > 0" );

            push (rho, 1.0/sy);
            push (s, oldParameters);
            push (y, oldg);
            // calculate new direction
            assert (s.size() == y.size()) :
                    "s.size: " + s.size() + " y.size: " + y.size();
            for(int i = s.size() - 1; i >= 0; i--) {
                alpha[i] =  ((Double)rho.get(i)).doubleValue() *
                        MatrixOps.dotProduct ( (double[])s.get(i), direction);
                MatrixOps.plusEquals (direction, (double[])y.get(i),
                        -1.0 * alpha[i]);
            }
            MatrixOps.timesEquals(direction, gamma);
            for(int i = 0; i < y.size(); i++) {
                double beta = (((Double)rho.get(i)).doubleValue()) *
                        MatrixOps.dotProduct((double[])y.get(i), direction);
                MatrixOps.plusEquals(direction,(double[])s.get(i),
                        alpha[i] - beta);
            }

            for (int i=0; i < oldg.length; i++) {
                oldParameters[i] = parameters[i];
                oldg[i] = g[i];
                direction[i] *= -1.0;
            }
            System.err.println("before linesearch: direction.gradient.dotprod: "+
                    MatrixOps.dotProduct(direction,g)+"\ndirection.2norm: " +
                    MatrixOps.twoNorm (direction) + "\nparameters.2norm: " +
                    MatrixOps.twoNorm(parameters));
            //TestMaximizable.testValueAndGradientInDirection (maxable, direction);
            step = lineMaximizer.optimize(direction, step);
            if (step == 0.0) { // could not step in this direction.
                g = null; // reset search
                step = 1.0;
                // xxx Temporary test; passed OK
//				TestMaximizable.testValueAndGradientInDirection (maxable, direction);
                throw new RuntimeException("Line search could not step in the current direction. " +
                        "(This is not necessarily cause for alarm. Sometimes this happens close to the maximum," +
                        " where the function may be very flat.)");
                //	return false;
            }
            optimizable.getParameters (parameters);
            optimizable.getValueGradient(g);
            System.err.println("after linesearch: direction.2norm: " +
                    MatrixOps.twoNorm (direction));
            double newValue = optimizable.getValue();

            // Test for terminations
            if(2.0*Math.abs(newValue-value) <= tolerance*
                    (Math.abs(newValue)+Math.abs(value) + eps)){
                System.err.println("Exiting L-BFGS on termination #1:\nvalue difference below tolerance (oldValue: " + value + " newValue: " + newValue);
                converged = true;
                return true;
            }
            double gg = MatrixOps.twoNorm(g);
            if(gg < gradientTolerance) {
                System.err.println("Exiting L-BFGS on termination #2: \ngradient="+gg+" < "+gradientTolerance);
                converged = true;
                return true;
            }
            if(gg == 0.0) {
                System.err.println("Exiting L-BFGS on termination #3: \ngradient==0.0");
                converged = true;
                return true;
            }
            System.err.println("Gradient = "+gg);
            iterations++;
            if (iterations > maxIterations) {
                System.err.println("Too many iterations in L-BFGS.java. Continuing with current parameters.");
                converged = true;
                return true;
                //throw new IllegalStateException ("Too many iterations.");
            }

            //end of iteration. call evaluator
            if (eval != null && !eval.evaluate (optimizable, iterationCount)) {
                System.err.println("Exiting L-BFGS on termination #4: evaluator returned false.");
                converged = true;
                return false;
            }
        }
        return false;
    }

    /** Resets the previous gradients and values that are used to
     * approximate the Hessian. NOTE - If the {@link de.iisys.ocr.optimizer.IFunction} object
     * is modified externally, this method should be called to avoid
     * IllegalStateExceptions. */
    public void reset () {
        g = null;
    }

    /**
     * Pushes a new object onto the queue l
     * @param l linked list queue of Matrix obj's
     * @param toadd matrix to push onto queue
     */
    private void push(LinkedList l, double[] toadd) {
        assert(l.size() <= m);
        if(l.size() == m) {
            // remove oldest matrix and add newset to end of list.
            // to make this more efficient, actually overwrite
            // memory of oldest matrix

            // this overwrites the oldest matrix
            double[] last = (double[]) l.get(0);
            System.arraycopy(toadd, 0, last, 0, toadd.length);
            Object ptr = last;
            // this readjusts the pointers in the list
            for(int i=0; i<l.size()-1; i++)
                l.set(i, (double[])l.get(i+1));
            l.set(m-1, ptr);
        }
        else {
            double [] newArray = new double[toadd.length];
            System.arraycopy (toadd, 0, newArray, 0, toadd.length);
            l.addLast(newArray);
        }
    }

    /**
     * Pushes a new object onto the queue l
     * @param l linked list queue of Double obj's
     * @param toadd double value to push onto queue
     */
    private void push(LinkedList l, double toadd) {
        assert(l.size() <= m);
        if(l.size() == m) { //pop old double and add new
            l.removeFirst();
            l.addLast(new Double(toadd));
        }
        else
            l.addLast(new Double(toadd));
    }


}

