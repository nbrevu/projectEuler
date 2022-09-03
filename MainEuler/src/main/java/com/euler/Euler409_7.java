package com.euler;

import com.euler.common.EulerUtils;

public class Euler409_7 {
	private final static int N=10_000_000;
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long e=EulerUtils.expMod(2l,N,MOD)-1;
		long lMinus1=0l;
		long f=(e*(e-1))%MOD;
		long l=0;
		long w=f;
		for (int i=3;i<=N;++i)	{
			long lMinus2=lMinus1;
			lMinus1=l;
			long wMinus1=w;
			f*=e+1-i;
			f%=MOD;
			long multiplier=((i-1)*(e+2-i))%MOD;
			long subtract=(multiplier*lMinus2)%MOD;
			l=(wMinus1+MOD-subtract)%MOD;
			w=(f+MOD-l)%MOD;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(w);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
