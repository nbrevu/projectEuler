package com.euler;

import static com.euler.common.EulerUtils.expMod;

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler437 {
	// Algo falla, pero no sé qué.
	private final static int LIMIT=10000;
	private final static List<Long> primes=Primes.listLongPrimes((long)LIMIT);
	
	private static boolean isPrimitiveRoot(long root,long prime)	{
		return expMod(root,(prime-1)/2,prime)==prime-1;
	}

	private static NavigableSet<Long> getPrimeFactors(long n,long base)	{
		NavigableSet<Long> result=new TreeSet<>();
		for (long p:primes) if ((n%p)==0)	{
			if ((p>base)&&isPrimitiveRoot(base,p)) result.add(p);
			do n/=p; while ((n%p)==0);
		}	else if (p*p>n) break;
		if ((n>base)&&isPrimitiveRoot(base,n)) result.add(n);
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
