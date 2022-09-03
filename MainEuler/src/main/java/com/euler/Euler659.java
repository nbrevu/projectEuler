package com.euler;

import java.math.RoundingMode;
import java.util.stream.LongStream;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler659 {
	private final static long LIMIT=LongMath.pow(10l,3);
	private final static long MOD=LongMath.pow(10l,18);
	
	private static long findMaxFactor(long in,long[] oddPrimes)	{
		for (long p:oddPrimes) if ((p*p)>in) return in;
		else if ((in%p)==0)	{
			in/=p;
			while ((in%p)==0) in/=p;
			if (in==1) return p;
		}
		return in;
	}
	
	public static void main(String[] args)	{
		// Pues está mal :(.
		/*
		 * Buscando sólo por 4k^2+1 no se encuentra la solución. Por ejemplo, para 4 sale 65 -> 13, sin embargo hay mejores soluciones:
		 * mcd(64+4,81+4) = 17.
		 * IDIOTEN, SUPER IDIOTEN, MEGA IDIOTEN, 4!=2.
		 */
		long tic=System.nanoTime();
		long maxExpected=4*LIMIT*LIMIT+1;
		long sq=LongMath.sqrt(maxExpected, RoundingMode.UP)+1;
		long[] oddPrimes=Primes.listLongPrimes(sq).stream().skip(1).mapToLong(Long::longValue).toArray();
		// LongStream.iterate(1,(long x)->x+1).limit(LIMIT).forEach(System.out::println);
		long result=LongStream.iterate(1,(long x)->x+1).limit(LIMIT).parallel()
			.map((long x)->findMaxFactor(x,oddPrimes))
			.reduce(0l,(long a,long b)->(a+b)%MOD);
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
