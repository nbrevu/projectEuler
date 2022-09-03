package com.euler;

import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler474_4 {
	private final static int FACT_OPERAND=IntMath.pow(10,6);
	private final static int DIGITS=5;
	private final static int REMAINDER=65432;
	private final static long MOD=LongMath.pow(10l,16)+61l;
	
	private static int timesInFactorial(int prime,int factorial)	{
		int result=0;
		while (factorial>=prime)	{
			int div=factorial/prime;
			result+=div;
			factorial=div;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		int p10=IntMath.pow(10,DIGITS);
		long[] f=new long[p10];
		long[] g=new long[p10];
		f[8]=1l;
		for (int p:Primes.listIntPrimes(FACT_OPERAND))	{
			if ((p==2)||(p==5)) continue;
			int e=timesInFactorial(p,FACT_OPERAND);
			for (int i=0;i<p10;i+=8) g[i]=0;
			for (int i=0;i<p10;i+=8) if (f[i]!=0)	{
				long t=i;
				for (int j=0;j<=e;++j,t=(t*p)%p10) g[(int)t]=(g[(int)t]+f[i])%MOD;
			}
			for (int i=8;i<p10;i+=8) f[i]=g[i];
		}
		System.out.println(f[REMAINDER]);
	}
}
