package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.List;

import com.euler.common.Primes.PrimeDecomposer;

/**
 * Standard solver (entry point of this library). Call Diophantine2dSolver.solve() with the appropriate arguments and forget about the details.
 */
public class Diophantine2dSolver {
	public static List<DiophantineSolution> solve(long a,long b,long c,long d,long e,long f,PrimeDecomposer decomposer)	{
		return solve(BigInteger.valueOf(a),BigInteger.valueOf(b),BigInteger.valueOf(c),BigInteger.valueOf(d),BigInteger.valueOf(e),BigInteger.valueOf(f),decomposer);
	}
	public static List<DiophantineSolution> solve(BigInteger a,BigInteger b,BigInteger c,BigInteger d,BigInteger e,BigInteger f,PrimeDecomposer decomposer)	{
		return solve(a,b,c,d,e,f,decomposer,false);
	}
	public static List<DiophantineSolution> solve(BigInteger a,BigInteger b,BigInteger c,BigInteger d,BigInteger e,BigInteger f,PrimeDecomposer decomposer,boolean skipRecursion)	{
		if ((a.signum()==0)&&(c.signum()==0))	{
			if (b.signum()==0) return new LinearEquation(d,e,f).solve();
			else return new SimpleHyperbolicEquation(b,d,e,f,decomposer).solveSpecific();
		}
		BigInteger det=b.multiply(b).subtract(Diophantine2dEquation.FOUR.multiply(a.multiply(c)));
		int signType=det.signum();
		if (signType==0) return new ParabolicEquation(a,b,d,e,f,decomposer).solveSpecific();
		RecursiveSolutionSource recursiveSolutions=skipRecursion?NoRecursiveSolutions.INSTANCE:RecursiveSolutionSource.getRecursiveSolution(a,b,c,d,e,det);
		return LegendreTransformation.applyLegendreTransformIfNecessary(a,b,c,d,e,f,det,decomposer,recursiveSolutions);
	}
}
