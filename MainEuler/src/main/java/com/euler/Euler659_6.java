package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler659_6 {
	private final static int LIMIT=IntMath.pow(10,3);
	
	private final static int PRIME_LIMIT=IntMath.pow(10,7);
	
	private static long[] calculateBruteForce(int limit,PrimeDecomposer decomposer)	{
		long[] result=new long[1+limit];
		for (int i=1;i<=limit;++i)	{
			DivisorHolder divisors=decomposer.decompose(4*i*i+1);
			long maxPrime=1l;
			for (LongCursor cursor=divisors.getFactorMap().keySet().cursor();cursor.moveNext();) maxPrime=Math.max(maxPrime,cursor.elem());
			result[i]=maxPrime;
		}
		return result;
	}
	
	// ¿Es posible que pueda hacer magia con Tonelli-Shanks? Espero que sí. Lo primero, comprobar que sólo hay primos 4k+1.
	private static long[] calculateWithAlgorithm(int limit,PrimeDecomposer decomposer)	{
		LongSet[] knownFactors=new LongSet[1+limit];
		for (int i=1;i<=limit;++i) knownFactors[i]=HashLongSets.newMutableSet();
		long[] result=new long[1+limit];
		for (int i=1;i<=limit;++i) 	{
			long maxPrime=1l;
			long base=4*i*i+1;
			if (!knownFactors[i].isEmpty()) for (LongCursor cursor=knownFactors[i].cursor();cursor.moveNext();)	{
				long p=cursor.elem();
				maxPrime=Math.max(maxPrime,p);
				do base/=p; while ((base%p)==0);
			}
			if (base>1)	{
				System.out.println("Para i="+i+" tengo que descomponer el "+base+"...");
				DivisorHolder divisors=decomposer.decompose(base);
				for (LongCursor cursor=divisors.getFactorMap().keySet().cursor();cursor.moveNext();)	{
					long p=cursor.elem();
					for (int j=(int)(i+p);j<=limit;j+=p) knownFactors[j].add(p);
					maxPrime=Math.max(maxPrime,p);
					if ((p%4)==3) System.out.println("ACHTUNG! Encontrado un primo inesperado, "+p+", cuando estaba descomponiendo "+base+" al evaluar el número "+i+".");
				}
			}
			result[i]=maxPrime;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(PRIME_LIMIT);
		long[] bf=calculateBruteForce(LIMIT,decomposer);
		long[] finesse=calculateWithAlgorithm(LIMIT,decomposer);
		for (int i=1;i<=LIMIT;++i) if (bf[i]!=finesse[i]) System.out.println("Para "+i+" me sale "+finesse[i]+", pero es "+bf[i]+".");
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
