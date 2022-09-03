package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.Arrays;

import com.euler.common.Convergents.Convergent;
import com.euler.common.FiniteContinuedFraction;
import com.euler.common.Primes.PrimeDecomposer;
import com.google.common.collect.Iterables;

public class EllipticalConvergentSolvableEquation extends ConvergentSolvableEquation	{
	private final static BigInteger MINUS_FOUR=BigInteger.valueOf(-4l);
	private final static BigInteger MINUS_ONE=BigInteger.valueOf(-1l);
	public EllipticalConvergentSolvableEquation(BigInteger a,BigInteger b,BigInteger c,BigInteger rightSide,PrimeDecomposer decomposer,BigInteger det)	{
		super(a,b,c,rightSide,decomposer,det);
	}
	@Override
	protected Iterable<Convergent> getConvergentForEquation(BigInteger p,BigInteger q,BigInteger r) {
		/*-
		 * "When the discriminant equals -4 and P = 2, a solution is (Y', k) = ((Q/2 − 1) / 2, -1)". 
		 * ARE YOU FUCKING KIDDING ME.
		 */
		if (MINUS_FOUR.equals(det)&&BigInteger.TWO.equals(p))	{
			BigInteger q2=q.divide(BigInteger.TWO);
			Convergent sol1=new Convergent(q2.subtract(BigInteger.ONE).divide(BigInteger.TWO),MINUS_ONE);
			Convergent sol2=new Convergent(q2.add(BigInteger.ONE).divide(BigInteger.TWO),MINUS_ONE);
			return Arrays.asList(sol1,sol2);
		}	else return Iterables.concat(BASE_SOLUTION,FiniteContinuedFraction.getFor(q.negate(),p.add(p)));
	}
}
