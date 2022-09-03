package com.euler;

import java.util.List;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler381 {
	// Wow, that was EASY. Another one at the first try...
	private final static long LIMIT=100000000l;
	private final static List<Long> primes=Primes.listLongPrimes(LIMIT);
	
	private static long function(long prime)	{
		// First result: always p-1
		// Second result: always 1
		// Therefore we only need to calculate from -3 to -5.
		long thirdResult=(EulerUtils.modulusInverse(prime-2,prime))%prime;
		long fourthResult=(thirdResult*EulerUtils.modulusInverse(prime-3,prime))%prime;
		long fifthResult=(fourthResult*EulerUtils.modulusInverse(prime-4,prime))%prime;
		return (thirdResult+fourthResult+fifthResult)%prime;
	}
	
	public static void main(String[] args)	{
		long res=0l;
		for (long p:primes.subList(2,primes.size())) res+=function(p);
		System.out.println(res);
	}
}
