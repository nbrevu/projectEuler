package com.euler;

import java.math.RoundingMode;
import java.util.BitSet;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler540_3 {
	private final static long N=3141592653589793l;
	
	private static long countBounded(long m,long[] firstPrimes,long limit)	{
		// Inclusion-exclusion over combinations of prime factors. No powers needed!
		long maxN=LongMath.sqrt(limit-m*m,RoundingMode.DOWN);
		maxN=Math.min(maxN,m);
		if ((m%2)==1) maxN/=2;
		long[] factors=DivisorHolder.getFromFirstPrimes(m,firstPrimes).getFactorMap().keySet().toLongArray();
		int howMany=factors.length;
		int maxComb=1<<howMany;
		long[] l=new long[1];
		long result=0;
		for (l[0]=0;l[0]<maxComb;++l[0])	{
			BitSet bitSet=BitSet.valueOf(l);
			long factor=1l;
			long sign=1;
			for (int i=bitSet.nextSetBit(0);i>=0;i=bitSet.nextSetBit(i+1))	{
				factor*=factors[i];
				sign=-sign;
			}
			result+=(maxN/factor)*sign;
		}
		return result;
	}
	
	private static long countMaximum(long m,long[] firstPrimes) {
		// if (m==1) return 0; // Not needed, I'm counting from m=2.
		long totient=DivisorHolder.getFromFirstPrimes(m,firstPrimes).getTotient();
		return ((m%2)==0)?totient:(totient/2);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long limitHalf=LongMath.sqrt(N/2,RoundingMode.DOWN);
		long limitFull=LongMath.sqrt(N,RoundingMode.DOWN);
		long[] firstPrimes=Primes.firstPrimeSieve(limitFull);
		long result=0;
		for (long i=2;i<=limitHalf;++i) result+=countMaximum(i,firstPrimes);
		for (long i=limitHalf+1;i<=limitFull;++i) result+=countBounded(i,firstPrimes,N);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
