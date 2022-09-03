package com.euler;

import com.euler.common.Primes;

public class Euler308_4 {
	private final static int N=10001;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int primeLimit=2*N*(int)Math.floor(Math.log(N));
		int[] primes=Primes.firstPrimeSieve(primeLimit);
		long result=22;
		long counted=1;
		for (int x=3;;++x)	{
			int prime=primes[x];
			boolean isPrime;
			int div;
			if (prime==0)	{
				isPrime=true;
				div=1;
			}	else	{
				isPrime=false;
				div=x/prime;
			}
			long inc1=x+div-1;
			long p1=6*x+2;
			long p2=x-div;
			long s=0;
			for (int i=div;i<x;++i) s+=x/i;
			result+=inc1+(p1*p2)+2*s;
			if (isPrime)	{
				++counted;
				if (counted>=N)	{
					result-=1+x;
					break;
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
