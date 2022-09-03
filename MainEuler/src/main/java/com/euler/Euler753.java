package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler753 {
	/*
	 * This program verifies what I had already proven: there is a solution if and only if -3 DOESN'T have a square root modulo p.
	 * 
	 * When there are solutions, the set of modular cubes is exactly the set of modular values.
	 */
	private final static int LIMIT=10000;
	
	private static boolean checkCond1(long p,long[] cubes) {
		for (int i=1;i<p;++i) for (int j=i+1;j<p;++j) if (((cubes[i]-cubes[j])%p)==0) return false;
		return true;
	}
	
	private static boolean checkCond2(long p)	{
		return EulerUtils.squareRootModuloPrime(p-3,p).isEmpty();
	}
	
	private static boolean checkCond3(long p)	{
		return p%3!=1;
	}
	
	public static void main(String[] args)	{
		long[] cubes=new long[1+LIMIT];
		for (int i=1;i<=LIMIT;++i) cubes[i]=i*(long)i*i;
		for (long p:Primes.listLongPrimes(LIMIT))	{
			System.out.println(p+"...");
			if (p<4) continue;
			boolean cond1=checkCond1(p,cubes);
			boolean cond2=checkCond2(p);
			boolean cond3=checkCond3(p);
			if ((cond1!=cond2)||(cond1!=cond3)) throw new RuntimeException("\tPara "+p+" no se cumple: cond1="+cond1+", cond2="+cond2+".");
		}
	}
}
