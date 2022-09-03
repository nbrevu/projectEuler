package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler743 {
	private final static long K=LongMath.pow(10l,8);
	private final static long N=LongMath.pow(10l,16);
	private final static long REPS=N/K;
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		/*
		 * The result is very clearly the sum, for i=0 to K/2, of K over (i,i,K-2i) multiplied times 2^((K-2i)*REPS).
		 * We can init the addend as 2^(K*REPS)=2^N. This is the first addend.
		 * Then:
		 * 	- We move from K over (i-1,i-1,K-2i+2) to K over (i,i,K-2i) by multiplying times (K-2i+1)*(K-2i+2) and dividing by i^2.
		 *  - We move from 2^((K-2i-2)*REPS) to 2^((K-2i)*REPS) by dividing by 2^(2*REPS).
		 *  - Obviously instead of divisions we multiply times the modular inverses.
		 */
		long tic=System.nanoTime();
		int maxIter=(int)(REPS/2);
		long[] inverses=EulerUtils.calculateModularInverses2(1+maxIter,MOD);
		long addend=EulerUtils.expMod(2l,N,MOD);
		long result=addend;
		long currentFactorToMultiply=K;
		//long inversePower=EulerUtils.modulusInverse(EulerUtils.expMod(2l,2*REPS,MOD),MOD);
		long inversePower=EulerUtils.expMod(2l,MOD-1-((2*REPS)%(MOD-1)),MOD);
		for (int i=1;i<=maxIter;++i)	{
			addend=(addend*currentFactorToMultiply)%MOD;
			--currentFactorToMultiply;
			addend=(addend*currentFactorToMultiply)%MOD;
			--currentFactorToMultiply;
			addend=(addend*inverses[i])%MOD;
			addend=(addend*inverses[i])%MOD;
			addend=(addend*inversePower)%MOD;
			result+=addend;
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
