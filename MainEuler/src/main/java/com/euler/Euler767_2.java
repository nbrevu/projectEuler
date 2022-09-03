package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.CombinatorialNumberCache;

public class Euler767_2 {
	/*
	 * If SIZE=2, K=10^8 and N=10^16, this is exactly problem 743.
	 * If SIZE=16, K=10^5 and N=10^16, this is problem 767.
	 */
	private final static int SIZE=2;
	private final static int K=3;//IntMath.pow(10,5);
	private final static long N=9;//LongMath.pow(10l,16);
	private final static long MOD=1_000_000_007l;
	
	private final static long REPS=N/K;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CombinatorialNumberCache cache=new CombinatorialNumberCache(SIZE);
		long[] sol1=new long[1+K];
		long combi=cache.get(SIZE,1);
		for (int i=0;i<=K;++i)	{
			long thisVal=0;
			for (int n=0;n<=i;++n) thisVal+=EulerUtils.expMod(combi,n*REPS,MOD);
			sol1[i]=thisVal%MOD;
		}
		long[] curSol=sol1;
		long[] nextSol=new long[1+K];
		System.out.println(Arrays.toString(curSol));
		for (int j=2;j<=SIZE;++j)	{
			combi=cache.get(SIZE,j);
			for (int i=0;i<=K;++i)	{
				long thisVal=0;
				int maxN=i/j;
				for (int n=0;n<=maxN;++n)	{
					thisVal+=EulerUtils.expMod(combi,n*REPS,MOD)*curSol[i-n*j];
					thisVal%=MOD;
				}
				nextSol[i]=thisVal;
			}
			long[] swap=curSol;
			curSol=nextSol;
			nextSol=swap;
		}
		System.out.println(Arrays.toString(curSol));
		long result=curSol[K];
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
