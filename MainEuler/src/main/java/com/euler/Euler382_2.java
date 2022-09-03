package com.euler;

import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;

public class Euler382_2 {
	private final static long N=LongMath.pow(10l,18);
	private final static long MOD=LongMath.pow(10l,9);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		LongMatrix matrix=new LongMatrix(11);
		matrix.assign(0,0,2l);
		matrix.assign(1,0,1l);
		matrix.assign(1,7,1l);
		matrix.assign(1,8,1l);
		matrix.assign(1,9,1l);
		matrix.assign(1,10,-1l);
		matrix.assign(2,1,1l);
		matrix.assign(2,9,1l);
		matrix.assign(3,0,1l);
		matrix.assign(3,1,1l);
		matrix.assign(3,2,1l);
		matrix.assign(3,3,1l);
		matrix.assign(4,0,1l);
		matrix.assign(5,1,1l);
		matrix.assign(6,2,1l);
		matrix.assign(7,4,1l);
		matrix.assign(8,5,1l);
		matrix.assign(9,6,1l);
		matrix.assign(10,10,1l);
		long[] vec=new long[] {8,3,1,7,4,1,0,2,0,0,1};
		LongMatrix lastMatrix=matrix.pow(N-6,MOD);
		long[] lastVec=lastMatrix.multiply(vec);
		long result=(lastVec[0]+lastVec[1]+lastVec[2]+lastVec[3])%MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
