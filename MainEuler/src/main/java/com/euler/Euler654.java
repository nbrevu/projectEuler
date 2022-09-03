package com.euler;

import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;

public class Euler654 {
	private final static int N=5000;
	private final static long M=LongMath.pow(10l,12);
	private final static long MOD=1000000007l;
	
	private static LongMatrix createBaseMatrix(int n)	{
		LongMatrix result=new LongMatrix(n-1);
		for (int i=0;i<n-1;++i) for (int j=0;j<=(n-2-i);++j) result.assign(i,j,1);
		return result;
	}
	
	private static long getResult(int n,long m,long mod)	{
		LongMatrix baseMatrix=createBaseMatrix(n);
		LongMatrix finalMatrix=baseMatrix.pow(m-1,mod);
		// The result is the product of this matrix, times the vector [1,1,...,1]. This is equivalent to adding all the elements in the matrix.
		long result=0;
		for (int i=0;i<n-1;++i) for (int j=0;j<n-1;++j) result+=finalMatrix.get(i,j);
		// If (n-1)^2<(2^63)/mod, we can just use one mod operation, at the end.
		return result%mod;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		System.out.println(getResult(N,M,MOD));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
