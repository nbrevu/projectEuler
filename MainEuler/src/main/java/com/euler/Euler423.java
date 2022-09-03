package com.euler;

import static com.euler.common.EulerUtils.expMod;

import com.euler.common.Primes;

public class Euler423 {
	private final static int N=50000000;
	private final static long PASCAL_2[]=new long[]{1,2,1};
	private final static long INITIALSUM_3=6+36+216;
	private final static long MOD=1000000007;
	
	private static long[] getNextPascalTriangleRow(long[] previousRow,long mod)	{
		int N=previousRow.length;
		long res[]=new long[N+1];
		res[0]=previousRow[0];
		res[N]=previousRow[N-1];
		for (int i=1;i<N;++i) res[i]=(previousRow[i-1]+previousRow[i])%mod;
		return res;
	}
	
	public static long getC(long total,long[] pascalRow,int lastValue,long mod)	{
		long product=6*expMod(5,total-lastValue-2,mod);
		long res=0;
		for (int i=lastValue;i>=0;--i)	{
			product=(product*5)%mod;
			long augend=(product*pascalRow[i])%mod;
			res=(res+augend)%mod;
		}
		return res;
	}
	
	public static void main(String[] args)	{
		long res=INITIALSUM_3;
		boolean[] composites=Primes.sieve(N);
		long[] pascalRow=PASCAL_2;
		int primesUpTo=2;
		for (int i=4;i<=N;++i)	{
			pascalRow=getNextPascalTriangleRow(pascalRow,MOD);
			if (!composites[i]) ++primesUpTo;
			long augend=getC(i,pascalRow,primesUpTo,MOD);
			res=(res+augend)%MOD;
			if ((i%500000)==0) System.out.println(""+i+": "+augend+" => "+res);
		}
		System.out.println(res);
	}
}
