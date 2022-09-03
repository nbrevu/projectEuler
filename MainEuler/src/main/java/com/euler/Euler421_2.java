package com.euler;

import java.util.function.LongConsumer;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler421_2 {
	private final static long PRIME_LIMIT=LongMath.pow(10l,8);
	private final static long NUMBER_LIMIT=LongMath.pow(10l,11);
	
	/*
	 * Works only when q divides p-1. For example, you can calculate the cube (i.e. 3rd) root of 7 because 7==1 (mod 3), but you can't calculate
	 * the fifth root of 9 because 9!==1 (mod 5). Also, q must be prime.
	 */
	private static class ModularRootCalculator	{
		private final long p;
		private final long q;
		private final LongSet unitRoots;
		public static ModularRootCalculator createCalculator(long p,long q)	{
			long nonResidue=findNonResidue(p,q);
			LongSet unitRoots=findUnitRoots(p,q,nonResidue);
			return new ModularRootCalculator(p,q,unitRoots);
		}
		public ModularRootCalculator(long p,long q,LongSet unitRoots)	{
			this.p=p;
			this.q=q;
			this.unitRoots=unitRoots;
		}
		public LongSet calculateRoots(long a)	{
			LongSet result=HashLongSets.newMutableSet();
			long baseRoot=findSingleRoot(a);
			for (LongCursor cursor=unitRoots.cursor();cursor.moveNext();) result.add((cursor.elem()*baseRoot)%p);
			return result;
		}
		private long findSingleRoot(long a)	{
			return williamsAlgorithm(p,q,a);
		}
		private static long findNonResidue(long p,long q)	{
			for (long n=2;n<p;++n) if (EulerUtils.expMod(n,q,p)!=1l) return n;
			throw new ArithmeticException("Can't find a non-residue.");
		}
		private static LongSet findUnitRoots(long p,long q,long nonResidue)	{
			LongSet result=HashLongSets.newMutableSet();
			long firstExponent=(p-1)/q;
			long firstRoot=EulerUtils.expMod(nonResidue,firstExponent,q);
			result.add(firstRoot);
			long currentValue=firstRoot;
			for (long i=2;i<=q;++i)	{
				currentValue=(currentValue*firstRoot)%p;
				result.add(currentValue);
			}
			return result;
		}
	}
	
	/*
	 * Slightly different notation because this comes from another paper :|. q is the prime, r is the root index, c is the residue.
	 */
	private static long williamsAlgorithm(long q,long r,long c)	{
		// ZUTUN! TODO! TEHDÄ!!!!!
		/*
		 * CONTINUE: 1501.04036-1.pdf, pages 2 and 3. It's not *that* complicated but it's still fucked up.
		 * Still better than AMM's casual "find a discrete logarithm" bullshit.
		 * Alternatively, try to find the Cipolla-Lehmer algorithm. How hard can it be? (Narrator's note: VERY hard).
		 */
		// Wait. There is a better way to do this. GO TO 421_3.
		return 0l;
	}
	
	private static LongSet cubeRoots(long a,long p)	{
		return ModularRootCalculator.createCalculator(p,3l).calculateRoots(a);
	}
	
	private static LongSet fifthRoots(long a,long p)	{
		return ModularRootCalculator.createCalculator(p,3l).calculateRoots(a);
	}
	
	private static LongSet fifteenthRoots(long a,long p)	{
		LongSet result=HashLongSets.newMutableSet();
		for (LongCursor cursor=cubeRoots(a,p).cursor();cursor.moveNext();) fifthRoots(cursor.elem(),p).forEach((LongConsumer)result::add);
		return result;
	}
	
	private static LongSet singleton(long in)	{
		LongSet result=HashLongSets.newMutableSet();
		result.add(in);
		return result;
	}
	
	private static LongSet getBaseRoots(long p)	{
		if (p<=5) return singleton(p-1);
		boolean fizz=(p%3)==1;
		boolean buzz=(p%5)==1;
		if (fizz&buzz) return fifteenthRoots(p-1,p);
		else if (fizz) return cubeRoots(p-1,p);
		else if (buzz) return fifthRoots(p-1,p);
		else return singleton(p-1);
	}
	
	private static long sumAppearances(long initial,long prime,long maximum)	{
		long gapCount=(maximum-initial)/prime;	// Not exact, but we want the floor.
		return prime*(1+gapCount);	// Warding off by one errors off, then multiplying times the prime itself.
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		for (long p:Primes.listLongPrimes(PRIME_LIMIT)) for (LongCursor cursor=getBaseRoots(p).cursor();cursor.moveNext();) result+=sumAppearances(cursor.elem(),p,NUMBER_LIMIT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
