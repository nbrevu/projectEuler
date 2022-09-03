package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler739_2 {
	// OK. It's OEIS A009776. See also https://en.wikipedia.org/wiki/Catalan%27s_triangle.
	private final static long MOD=LongMath.pow(10l,9)+7;
	private final static int GOAL=IntMath.pow(10,8);
	
	private static long[] calculateCatalanTriangleRow(int n,long mod)	{
		long[] result=new long[1+n];
		long[] inverses=new long[n+2];
		for (int i=1;i<inverses.length;++i) inverses[i]=EulerUtils.modulusInverse(i,mod);
		result[0]=1l;
		for (int k=1;k<=n;++k)	{
			long val=result[k-1];
			val*=n+k;
			val%=mod;
			val*=n-k+1;
			val%=mod;
			val*=inverses[k];
			val%=mod;
			val*=inverses[n-k+2];
			val%=mod;
			result[k]=val;
		}
		return result;
	}
	private static long[] calculateLucasNumbers(int n,long mod)	{
		long[] result=new long[n];
		result[0]=1;
		result[1]=3;
		for (int i=2;i<n;++i) result[i]=(result[i-1]+result[i-2])%mod;
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] lucas=calculateLucasNumbers(GOAL,MOD);
		long[] triangular=calculateCatalanTriangleRow(GOAL-2,MOD);
		long result=0l;
		for (int i=1;i<GOAL;++i)	{
			result+=lucas[i]*triangular[GOAL-1-i];
			result%=MOD;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
