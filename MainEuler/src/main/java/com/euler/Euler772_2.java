package com.euler;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler772_2 {
	public final static long LIMIT=LongMath.pow(10l,8);
	public final static long MOD=1_000_000_007l;
	
	// Someone in the comment says their solution takes 59.75s. Using python. Impossible not to laugh.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] primes=Primes.listLongPrimesAsArray(LIMIT);
		long result=2l;
		for (long prime:primes)	{
			long q=prime;
			for(;;)	{
				long qq=q*prime;
				if (qq>LIMIT)	{
					result=(result*q)%MOD;
					break;
				}	else q=qq;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
