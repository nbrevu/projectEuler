package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.List;

/**
 * Objects of this class transform standalone fixed solutions into infinite recursive solutions. It's an interface so that a dummy, non-recursive
 * solution generating object can be used in place where needed.
 */
public interface RecursiveSolutionSource {
	public List<DiophantineSolution> getSolutions(List<FixedSolution> baseSolutions);
	public static RecursiveSolutionSource getRecursiveSolution(BigInteger a,BigInteger b,BigInteger c,BigInteger d,BigInteger e,BigInteger det)	{
		if (det.signum()>0)	{
			BigInteger sq=det.sqrt();
			/*
			 * If det is positive and not a perfect square then the equation is hyperbolic but it's not one of the special cases.
			 * In these cases we can generate recursive solutions.
			 */
			if (!sq.multiply(sq).equals(det)) return new RecursiveSolutionGenerator(a,b,c,d,e,det);
		}
		return NoRecursiveSolutions.INSTANCE;
	}
}
