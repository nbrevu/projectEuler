package com.euler;

import com.euler.common.BernoulliNumberModCache;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler487_3 {
	private final static long BASE=LongMath.pow(10l,12);
	private final static int POWER=10000;
	private final static int LIMIT_MIN=2000000000;
	private final static int LIMIT_MAX=2000002000;
	
	private static long getResult(long n,int k,long mod)	{
		BernoulliNumberModCache bernoulliCalculator=new BernoulliNumberModCache(mod);
		long s1=bernoulliCalculator.getFaulhaberSum(k,n);
		s1=(s1*((n+1)%mod))%mod;
		long s2=bernoulliCalculator.getFaulhaberSum(k+1,n);
		long result=s1-s2;
		if (result<0) result+=mod;
		return result;
	}
	
	public static void main(String[] args)	{
 		long tic=System.nanoTime();
		long result=0;
		boolean[] composites=Primes.sieve(LIMIT_MAX);
		for (int i=LIMIT_MIN;i<=LIMIT_MAX;++i) if (!composites[i]) result+=getResult(BASE,POWER,i);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
