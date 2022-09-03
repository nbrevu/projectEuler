package com.euler;

import java.util.Arrays;

import com.euler.common.Primes;

public class Euler537 {
	private final static int K=20000;
	private final static int N=20000;
	private final static long MOD=1004535809l;
	
	private static long[] getPiCounters(int upTo)	{
		int limit=(int)(Math.ceil(1.2*(upTo+1)*Math.log(upTo+1)));
		boolean[] composites=Primes.sieve(limit);
		long[] result=new long[1+upTo];
		int index=0;
		for (int i=1;i<=limit;++i)	{
			if (!composites[i])	{
				++index;
				if (index>upTo) break;
			}
			++result[index];
		}
		if (index<=upTo) throw new RuntimeException();	// I don't think this is going to happen.
		return result;
	}
	
	private static long solve(int k,int n,long mod)	{
		long[] a1=getPiCounters(n);
		long[] prev=Arrays.copyOf(a1,a1.length);
		long[] current=new long[prev.length];
		for (int m=2;m<k;++m)	{
			if ((m%100)==0) System.out.println(""+m+"...");
			for (int i=0;i<=n;++i)	{
				current[i]=0;
				for (int j=0;j<=i;++j)	{
					current[i]+=prev[j]*a1[i-j];
					current[i]%=mod;
				}
			}
			long[] swapper=current;
			current=prev;
			prev=swapper;
		}
		// For the last value of m we only need the last element.
		long result=0;
		for (int j=0;j<=n;++j)	{
			result+=prev[j]*a1[n-j];
			result%=mod;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=solve(K,N,MOD);
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
