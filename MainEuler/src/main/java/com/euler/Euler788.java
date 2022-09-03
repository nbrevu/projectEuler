package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils.CombinatorialNumberModCache;
import com.google.common.math.IntMath;

public class Euler788 {
	private final static int N=2022;
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long exp9[]=new long[1+N];
		exp9[0]=1l;
		for (int i=1;i<=N;++i) exp9[i]=(exp9[i-1]*9)%MOD;
		CombinatorialNumberModCache cache=new CombinatorialNumberModCache(N,MOD);
		long result=0l;
		for (int i=1;i<=N;++i)	{
			int j0=IntMath.divide(i-1,2,RoundingMode.UP);
			long thisResult=0;
			for (int j=j0+1;j<i;++j) thisResult+=(cache.get(i-1,j)*exp9[i-1-j])%MOD;
			thisResult=(thisResult*10)+cache.get(i-1,j0)*exp9[i-1-j0];
			result+=thisResult;
			result%=MOD;
		}
		result=(9*result)%MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
