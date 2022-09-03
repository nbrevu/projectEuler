package com.euler;

import com.google.common.math.LongMath;

public class Euler718_2 {
	private final static int P=4;
	
	private final static long P1=LongMath.pow(17,P);
	private final static long P2=LongMath.pow(19,P);
	private final static long P3=LongMath.pow(23,P);
	// Frobenius numbers calculated using Mathematica.
	private final static long[] FROBENIUS=new long[] {176l, 18422l, 1376626l, 110608186l, 9774616654l, 869836853153l};
	
	// This can be solved using Bézout's Lemma. Or CRT, maybe. I believe.
	private static boolean canSolve2(long n)	{
		for (n-=P2;n>=0;n-=P2) if (((n%P1)==0)&&(n>0)) return true;
		return false;
	}
	
	private static boolean cantSolve(long n)	{
		for (n-=P3;n>=0;n-=P3) if (canSolve2(n)) return false;
		return true;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		for (long i=1;i<=FROBENIUS[P-1];++i) if (cantSolve(i)) result+=i;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
