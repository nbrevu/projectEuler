package com.euler;

import java.math.RoundingMode;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler501 {
	private final static long LIMIT=1000000000000l;
	
	/*
	 * ACHTUNG!!!!! What Lucy_hedgehog gives, Lucy_hedgehog takes away. I have a prime *sum* function, but what I need here
	 * is a prime *count* function :(.
	 * 
	 * Look for: meissel-Lehmer; http://www.dtc.umn.edu/~odlyzko/doc/arch/meissel.lehmer.pdf; http://acganesh.com/blog/2016/12/23/prime-counting
	 */
	
	// Mi siguiente sugerencia es usar Mathematica y su estupentástica función PrimePi[].
	public static long count8DivCombinations(long limit)	{
		long result=0;
		long sqLimit=LongMath.sqrt(limit,RoundingMode.CEILING);
		NavigableSet<Long> primes=new TreeSet<>(Primes.listLongPrimes(sqLimit));
		// Numbers of the form p1·p2·p3
		for (long p:primes)	{
			NavigableSet<Long> biggerPrimes=primes.tailSet(p,false);
			for (long q:biggerPrimes)	{
				long primeLimit=limit/(p*q);
				long toAdd;
				if (primeLimit<=sqLimit) toAdd=primes.headSet(primeLimit,true).size();
				else toAdd=Primes.sumPrimes(primeLimit);
				if (primeLimit>q) --toAdd;
				if (primeLimit>p) --toAdd;
				result+=toAdd;
			}
		}
		// Numbers of the form p1^3·p2
		for (long p:primes)	{
			long pCube=p*p*p;
			if (pCube>limit) break;
			long primeLimit=limit/pCube;
			long toAdd;
			if (primeLimit<=sqLimit) toAdd=primes.headSet(primeLimit,true).size();
			else toAdd=Primes.sumPrimes(primeLimit);
			if (primeLimit>p) --toAdd;
			result+=toAdd;
		}
		// Numbers of the form p1^7.
		for (long p:primes)	{
			long p2=p*p;
			long p4=p2*p2;
			long p7=p*p2*p4;
			if (p7>limit) break;
			++result;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=count8DivCombinations(LIMIT);
		System.out.println(result);
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))/1e9;
		System.out.println("Used "+seconds+" seconds.");
	}
}
