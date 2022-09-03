package com.euler;

import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;

public class Euler356_2 {
	private final static long MOD=LongMath.pow(10l,8);
	private final static long EXP=987654321l;
	
	private static LongMatrix createMatrix(int n,long mod)	{
		LongMatrix result=new LongMatrix(3);
		result.assign(0,1,1l);
		result.assign(1,2,1l);
		result.assign(2,0,mod-n);
		result.assign(2,2,(1l<<n)%mod);
		return result;
	}
	
	// Eigenvalues, BITCHES.
	private static long getFloorResult(int n,long exp,long mod)	{
		LongMatrix matrix=createMatrix(n,mod);
		LongMatrix pow=matrix.pow(exp,mod);
		long trace=pow.get(0,0)+pow.get(1,1)+pow.get(2,2);
		return (trace-1)%mod;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0l;
		for (int i=1;i<=30;++i) result+=getFloorResult(i,EXP,MOD);
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
