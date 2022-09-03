package com.euler;

import static com.euler.common.EulerUtils.expMod;
import static com.euler.common.EulerUtils.gcd;

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler437_2 {
	// Algo falla, pero no sé qué.
	private final static int LIMIT=10000;
	private final static List<Long> primes=Primes.listLongPrimes((long)LIMIT);
	
	private static boolean isPrimitiveRoot(long root,long prime)	{
		if (prime>=root) return false;
		else if (gcd(root,prime)!=1) return false;
		for (long exp:Primes.getDistinctPrimeFactors(prime-1,primes)) if (expMod(root,(prime-1)/exp,prime)==1) return false;
		return true;
	}
	
	private static NavigableSet<Long> getPrimeFactors(long n,long base)	{
		NavigableSet<Long> result=new TreeSet<>();
		for (long p:Primes.getDistinctPrimeFactors(n,primes)) if ((!result.contains(p))&&isPrimitiveRoot(base,p)) result.add(p);
		return result;
	}
	
	public static void main(String[] args)	{
		NavigableSet<Long> primesWithPrimRoot=new TreeSet<>();
		for (long l=3;l<=LIMIT*100;++l)	{
			long sigil=l*(l-1)-1;
			primesWithPrimRoot.addAll(getPrimeFactors(sigil,l));
		}
		primesWithPrimRoot=primesWithPrimRoot.headSet((long)LIMIT,false);
		System.out.println(primesWithPrimRoot.size());
		System.out.println(primesWithPrimRoot);
	}
}
