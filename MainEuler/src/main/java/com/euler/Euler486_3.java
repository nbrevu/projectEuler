package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler486_3 {
	private final static long LIMIT=LongMath.pow(10l,18);

	private final static long MOD=87654321l;	// Conveniently, gcd(200*MAX_EXP,MOD) = 1.
	private final static long MAX_EXP=9732496l;	// =tot(87654321)/6, first value for which 64^i=1 (mod 87654321).
	private final static long[] A=new long[] {114,82,50,18,-16,-52};
	private final static long INVERSE=EulerUtils.modulusInverse(200l*MAX_EXP,MOD);
	private final static long BIG_CYCLE=6*MAX_EXP*MOD;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0l;
		long exp=1;	// This will store the 2^(6*b+k+1) values.
		long additionalTerm=0;	// This will store the 200*b values.
		for (long b=0;b<MAX_EXP;++b)	{
			for (int k=0;k<6;++k)	{
				exp=(exp+exp)%MOD;
				long finalTerm=exp+A[k]+MOD-additionalTerm;
				long d=(INVERSE*finalTerm)%MOD;
				long base=k+6*(b+MAX_EXP*d);
				result+=LongMath.divide(LIMIT-base,BIG_CYCLE,RoundingMode.UP);
			}
			additionalTerm=(additionalTerm+200)%MOD;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
