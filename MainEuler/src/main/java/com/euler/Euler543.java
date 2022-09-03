package com.euler;

import java.util.List;

import com.euler.common.Primes;

public class Euler543 {
	// D'oh! Esto no es lo que pensaba que había que hacer.
	private static long[] sumCases(int max)	{
		long[] result=new long[1+max];
		long[] swapper=new long[1+max];
		List<Integer> primes=Primes.listIntPrimes(max);
		primes=primes.subList(1,primes.size());
		for (int i=0;i<=max;i+=2) result[i]=1;
		for (int p:primes)	{
			System.arraycopy(result,0,swapper,0,1+max);
			for (int j=p;j<=max;++j) for (int k=p;k<=j;k+=p) swapper[j]+=result[j-k];
			long[] exchange=swapper;
			swapper=result;
			result=exchange;
		}
		return result;
	}
	
	private static long sumArray(long[] in,int upTo)	{
		long result=0l;
		for (int i=1;i<upTo;++i) result+=in[i];
		return result;
	}
	
	private static long sumArray(long[] in)	{
		return sumArray(in,in.length);
	}
	
	public static void main(String[] args)	{
		System.out.println(sumArray(sumCases(10)));
		System.out.println(sumArray(sumCases(100)));
		System.out.println(sumArray(sumCases(1000)));
	}
}
