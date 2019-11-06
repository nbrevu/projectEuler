package com.euler;

import java.math.RoundingMode;

import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler157 {
	private final static int N=9;
	
	private static long[] get10nDivs(int n)	{
		long[] result=new long[(1+n)*(1+n)];
		result[0]=1l;
		for (int i=1;i<=n;++i) result[i]=2*result[i-1];
		for (int i=n+1;i<result.length;++i) result[i]=5*result[i-n-1];
		return result;
	}
	
	private static int countDivs(long in,int[] primeList)	{
		// We can skip the typical prime map creation, and just multiply in place. We will multiply times 1 a lot of times but it's still faster.
		int result=1;
		for (int p:primeList)	{
			if (p*p>in) break;
			int counter=0;
			while ((in%p)==0)	{
				in/=p;
				++counter;
			}
			result*=1+counter;
		}
		if (in>1) result*=2;	// Unaccounted prime!
		return result;
	}
	
	private static long solve()	{
		int[] primeList=Primes.listIntPrimesAsArray(IntMath.sqrt(2*IntMath.pow(10,N),RoundingMode.DOWN));
		long result=0;
		long pow10=1;
		for (int i=1;i<=N;++i)	{
			pow10*=10;
			long[] divs=get10nDivs(i);
			// There are two cases of pairs of divisors of 10^n which are coprime: either 1 against anything...
			for (int j=0;j<divs.length;++j) result+=countDivs(pow10+pow10/divs[j],primeList);
			// ... or powers of 2 against powers of 5.
			for (int j=1;j<=i;++j) for (int k=i+1;k<divs.length;k+=i+1) result+=countDivs(pow10/divs[j]+pow10/divs[k],primeList);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler157::solve);
	}
}