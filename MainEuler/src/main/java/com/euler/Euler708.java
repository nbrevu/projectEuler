package com.euler;

import com.euler.common.Primes;
import com.google.common.math.IntMath;

public class Euler708 {
	private final static int LIMIT=IntMath.pow(10,8);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		long result=1l;	// Skipping 1...
		for (int i=2;i<=LIMIT;++i)	{
			int pow=0;
			int pointer=i;
			for (;;)	{
				++pow;
				int prime=firstPrimes[pointer];
				if (prime==0) break;
				else pointer/=prime;
			}
			result+=1l<<pow;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
