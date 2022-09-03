package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler797_5 {
	private final static int N=10_000_000;
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] primes=Primes.firstPrimeSieve((long)N);
		long[] values=new long[1+N];
		long[] primitives=new long[1+N];
		long[] primitiveInverses=new long[1+N];
		values[1]=2l;
		primitives[1]=1l;
		primitiveInverses[1]=1l;
		long lastPower=1l;
		int show=1000;
		for (int i=2;i<=N;++i)	{
			if (i>=show)	{
				System.out.println(i+"...");
				show+=1000;
			}
			DivisorHolder decomposition=DivisorHolder.getFromFirstPrimes(i,primes);
			lastPower=(2*lastPower+1)%MOD;
			long primitive=lastPower;
			long[] divisors=decomposition.getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(divisors);
			for (long div:divisors) if (div!=i) primitive=(primitive*primitiveInverses[(int)div])%MOD;
			primitives[i]=primitive;
			primitiveInverses[i]=EulerUtils.modulusInverse(primitive,MOD);
			long value=0;
			for (int j=0;j<divisors.length-2;++j) for (int k=j+1;k<divisors.length-1;++k)	{
				long d1=divisors[j];
				long d2=divisors[k];
				long lcm=d1*d2/EulerUtils.gcd(d1,d2);
				if (lcm==i)	{
					value+=primitives[(int)d2]*values[(int)d1];
					value%=MOD;
				}
			}
			value*=1+primitive;
			value%=MOD;
			for (int j=0;j<divisors.length-1;++j)	{
				value+=primitive*values[(int)divisors[j]];
				value%=MOD;
			}
			values[i]=value;
		}
		System.out.println(Arrays.toString(values));
		long result=0l;
		for (int i=1;i<=N;++i) result+=values[i];
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
