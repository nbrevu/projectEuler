package com.euler;

import java.util.Locale;

import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;

public class Euler777_4 {
	private final static long LIMIT=LongMath.pow(10l,6);
	
	private static double f1(long a)	{
		// b=1, case to be treated separately.
		return 2*a-1.5*(a+1);
	}
	
	private static double g1(long a)	{
		// b=1, case to be treated separately.
		return ((2*a-3*(a+1))+4)*0.25;
	}
	
	private static double sumF(double a,long bMultiplier,long limit)	{
		long howManyB=limit/bMultiplier;
		if (howManyB==0) return 0;
		long triangB=(howManyB*(howManyB+1))/2;
		long sumB=bMultiplier*triangB;
		return 2*a*sumB-1.5*(a*howManyB+sumB);
	}
	
	private static double sumG(double a,long bMultiplier,long limit)	{
		long howManyB=limit/bMultiplier;
		if (howManyB==0) return 0;
		long triangB=(howManyB*(howManyB+1))/2;
		long sumB=bMultiplier*triangB;
		return (2*a*sumB-3*a*howManyB-3*sumB+4*howManyB)*0.25;
	}
	
	private static double separatedSum(long a,Primes.PrimeFactorsLattice factors,long gFactor)	{
		double result=0;
		for (LongCursor toAdd=factors.positiveMöbius.cursor();toAdd.moveNext();)	{
			long prime=toAdd.elem();
			result+=sumF(a,prime,LIMIT);
			result-=sumF(a,gFactor*prime,LIMIT);
			result+=sumG(a,gFactor*prime,LIMIT);
		}
		for (LongCursor toSubtract=factors.negativeMöbius.cursor();toSubtract.moveNext();)	{
			long prime=toSubtract.elem();
			result-=sumF(a,prime,LIMIT);
			result+=sumF(a,gFactor*prime,LIMIT);
			result-=sumG(a,gFactor*prime,LIMIT);
		}
		result-=f1(a);
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double result=0;
		Primes.PrimeFactorsLatticeGenerator latticeGenerator=new Primes.PrimeFactorsLatticeGenerator(LIMIT);
		for (long a=2;a<=LIMIT;++a)	{
			Primes.PrimeFactorsLattice factors=latticeGenerator.getFor(a);
			if ((a%10)==0)	{
				for (LongCursor toAdd=factors.positiveMöbius.cursor();toAdd.moveNext();) result+=sumG(a,toAdd.elem(),LIMIT);
				for (LongCursor toSubtract=factors.negativeMöbius.cursor();toSubtract.moveNext();) result-=sumG(a,toSubtract.elem(),LIMIT);
				result-=g1(a);
			}	else if ((a%5)==0) result+=separatedSum(a,factors,2);
			else if ((a%2)==0) result+=separatedSum(a,factors,5);
			else result+=separatedSum(a,factors,10);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.9e",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
