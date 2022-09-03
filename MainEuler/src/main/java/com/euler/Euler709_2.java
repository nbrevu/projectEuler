package com.euler;

import com.euler.common.EulerUtils.CombinatorialNumberModCache;

public class Euler709_2 {
	private final static int SIZE=24680;
	private final static long MOD=1020202009l;
	
	public static void main(String[] args)	{
		/*
		773479144
		Elapsed 4.307297875000001 seconds.
		 */
		long tic=System.nanoTime();
		CombinatorialNumberModCache combinatorials=new CombinatorialNumberModCache(SIZE-1,MOD);
		long[] results=new long[1+SIZE];
		results[0]=1l;
		results[1]=1l;
		results[2]=1l;
		for (int i=3;i<=SIZE;++i)	{
			for (int j=0;j<i;j+=2)	{
				long conv=(results[j]*results[i-1-j])%MOD;
				results[i]+=conv*combinatorials.get(i-1,j);
				results[i]%=MOD;
			}
		}
		long result=results[SIZE];
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
