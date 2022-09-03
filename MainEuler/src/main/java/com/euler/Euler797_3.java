package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;

public class Euler797_3 {
	private final static int N=20;
	
	// This works, but it's O(n^2)*log(n) :|.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] primes=Primes.firstPrimeSieve((long)N);
		long[] values=new long[1+N];
		long[] primitiveValues=new long[1+N];
		values[1]=2l;
		primitiveValues[1]=1l;
		for (int i=2;i<=N;++i)	{
			DivisorHolder h=DivisorHolder.getFromFirstPrimes(i,primes);
			long primitive=(1l<<i)-1;
			for (LongCursor cursor=h.getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				int div=(int)cursor.elem();
				if (div!=i) primitive/=primitiveValues[div];
			}
			primitiveValues[i]=primitive;
			for (int j=N;j>=1;--j)	{
				int k=i*j/EulerUtils.gcd(i,j);
				if (k<=N) values[k]+=values[j]*primitive;
			}
		}
		long sum=0l;
		System.out.println(Arrays.toString(values));
		for (int i=1;i<=N;++i) sum+=values[i];
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
