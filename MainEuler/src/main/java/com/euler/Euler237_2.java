package com.euler;

import com.euler.common.LongMatrix;

public class Euler237_2 {
	private static long N=1000000000000l;
	private static long MOD=100000000l;
	
	private static LongMatrix getBaseMatrix()	{
		LongMatrix matrix=new LongMatrix(4);
		matrix.assign(0,0,2);
		matrix.assign(0,1,2);
		matrix.assign(0,2,-2);
		matrix.assign(0,3,1);
		matrix.assign(1,0,1);
		matrix.assign(1,1,0);
		matrix.assign(1,2,0);
		matrix.assign(1,3,0);
		matrix.assign(2,0,0);
		matrix.assign(2,1,1);
		matrix.assign(2,2,0);
		matrix.assign(2,3,0);
		matrix.assign(3,0,0);
		matrix.assign(3,1,0);
		matrix.assign(3,2,1);
		matrix.assign(3,3,0);
		return matrix;
	}

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		LongMatrix M=getBaseMatrix().pow(N-4,MOD);
		long result=(8*M.get(0,0)+4*M.get(0,1)+M.get(0,2)+M.get(0,3))%MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
