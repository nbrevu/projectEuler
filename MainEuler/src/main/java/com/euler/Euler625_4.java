package com.euler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler625_4 {
	private final static long PRIME_LIMIT=LongMath.pow(10l,4);
	private final static long LIMIT=LongMath.pow(10l,7);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<Long> primes=Primes.listLongPrimes(PRIME_LIMIT);
		SortedSet<Long> result=new TreeSet<>();
		result.add(1l);
		for (long prime:primes)	{
			Set<Long> toAdd=new HashSet<>();
			for (long l:result)	{
				long initialNumber=l*prime;
				if (initialNumber>LIMIT) break;
				for (long adding=initialNumber;adding<=LIMIT;adding*=prime) toAdd.add(adding);
			}
			result.addAll(toAdd);
		}
		System.out.println(result.size());
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
