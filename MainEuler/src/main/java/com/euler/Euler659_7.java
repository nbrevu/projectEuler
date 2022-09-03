package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler659_7 {
	private final static int LIMIT=IntMath.pow(10,7);
	private final static long MOD=LongMath.pow(10l,18);
	
	private static long[] calculateWithAlgorithm(int limit)	{
		boolean[] composites=Primes.sieve(2*limit+1);
		LongSet[] knownFactors=new LongSet[1+limit];
		for (int i=1;i<=limit;++i) knownFactors[i]=HashLongSets.newMutableSet();
		boolean add8=true;
		for (long p=5;p<composites.length;p+=(add8?8:4),add8=!add8) if (!composites[(int)p])	{
			long q=(p-1)/2;
			long rem=p-((q*q)%p);
			// Tonelli-Shanks saves the day yet again!
			LongSet squareRoots=EulerUtils.squareRootModuloPrime(rem,p);
			for (LongCursor cursor=squareRoots.cursor();cursor.moveNext();)	{
				long candidate=cursor.elem();
				for (long j=candidate;j<knownFactors.length;j+=p) knownFactors[(int)j].add(p);
			}
		}
		long[] result=new long[1+limit];
		for (int i=1;i<=limit;++i)	{
			long base=4l*i*i+1;
			long maxPrime=1l;
			for (LongCursor cursor=knownFactors[i].cursor();cursor.moveNext();)	{
				long p=cursor.elem();
				maxPrime=Math.max(maxPrime,p);
				do base/=p; while ((base%p)==0);
			}
			result[i]=Math.max(maxPrime,base);	// Normally it would be guarded by "if (base!=1)", but obviously the condition works equally well.
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] maxPrimes=calculateWithAlgorithm(LIMIT);
		long result=0l;
		for (int i=1;i<=LIMIT;++i) result=(result+maxPrimes[i])%MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
