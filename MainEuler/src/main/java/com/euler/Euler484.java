package com.euler;

import com.euler.common.Primes;

public class Euler484 {
	private final static int LIMIT=500;
	private final static int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
	
	private final static int[] getDerivatives(int limit)	{
		int[] result=new int[1+limit];
		for (int i=2;i<=limit;++i)	{
			int p=firstPrimes[i];
			if (p==0) result[i]=1;
			else	{
				int q=i/p;
				result[i]=result[q]*p+result[p]*q;
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		int[] derivatives=getDerivatives(LIMIT);
		for (int i=2;i<=LIMIT;++i) System.out.println(""+i+": "+derivatives[i]);
	}
}
