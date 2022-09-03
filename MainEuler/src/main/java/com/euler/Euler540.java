package com.euler;

import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler540 {
	private final static long N=LongMath.pow(10l,6);
	/*
	 * Considering how Pythagorean triples appear, basically this is about counting the amount of pairs (m,n) so that:
	 *  1) gcd(m,n) = 1.
	 *  2) one of (m,n) is even.
	 *  3) m^2+n^2 <= LIMIT.
	 *  
	 * One way to count them is to iterate over m and count all the possible values 1<=n<=m that match the first two criteria,
	 * then work carefully around the limit. We can call this f(m).
	 * 
	 * Hypothesis: f(m) = phi(m) if m is even, or phi(m)/2 if m is odd (special case: f(1)=0).
	 */
	
	private static int bruteForce(long m,long limit)	{
		long maxN=LongMath.sqrt(limit-m*m,RoundingMode.DOWN);
		maxN=Math.min(maxN,m);
		long initialValue=((m%2)==0)?1:2;
		int count=0;
		for (long n=initialValue;n<=maxN;n+=2) if (EulerUtils.gcd(n,m)==1) ++count;
		return count;
	}
	
	private static int theoretical(long m,long[] firstPrimes) {
		if (m==1) return 0;
		int totient=(int)DivisorHolder.getFromFirstPrimes(m,firstPrimes).getTotient();
		return ((m%2)==0)?totient:(totient/2);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long limitHalf=LongMath.sqrt(N/2,RoundingMode.DOWN);
		long limitFull=LongMath.sqrt(N,RoundingMode.DOWN);
		long[] firstPrimes=Primes.firstPrimeSieve(limitHalf);
		int result=0;
		for (long i=1;i<=limitHalf;++i) result+=theoretical(i,firstPrimes);
		for (long i=limitHalf+1;i<=limitFull;++i) result+=bruteForce(i,N);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
