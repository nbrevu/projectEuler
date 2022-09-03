package com.euler;

import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;

public class Euler804 {
	private final static long N=1000000;
	
	public static void main(String[] args)	{
		long count=0;
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(1_000_000);
		for (long i=1;i<=N;++i)	{
			int sols=Diophantine2dSolver.solve(1,1,41,0,0,-i,decomposer).size();
			if (sols==0) continue;
			System.out.println(String.format("f(%d)=%d ",i,sols));
			count+=sols;
		}
		System.out.println(count);
	}
}
