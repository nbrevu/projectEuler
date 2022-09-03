package com.euler;

import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;

public class Euler670_2 {
	private final static long EXP=LongMath.pow(10l,16);
	private final static long MOD=1_000_004_321l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long a0=1l;
		long c0=4l;
		long e0=4l;
		long g0=4l;
		long a1=4*a0;
		long b1=3*c0;
		long c1=3*a1+3*e0;
		long d1=3*c0;
		long e1=3*a1+3*g0;
		long f1=3*c0;
		long g1=3*a1;
		long a2=3*a1+2*b1;
		long b2=2*c1+2*d1+3*e0;
		long c2=3*a2+2*d1+2*e1+3*g0;
		long d2=2*c1+3*e0+2*f1;
		long e2=3*a2+2*d1+2*g1;
		long f2=2*c1;
		long g2=3*a2;
		long a3=3*a2+2*b2;
		long b3=2*c2+2*d2+2*e1+2*f1+3*g0;
		long c3=3*a3+2*d2+2*e2+2*f1+2*g1;
		long d3=2*c2+2*e1+2*f2;
		long e3=3*a3+2*d2+2*g2;
		long f3=2*c2;
		long g3=3*a3;
		/*
		 * Indices:
		 * 0-6: A-G.
		 * 7-9: E-G (-1)
		 * 10: G (-2)
		 */
		LongMatrix matrix=new LongMatrix(11);
		// A.
		matrix.assign(0,0,3l);
		matrix.assign(0,1,2l);
		// B.
		matrix.assign(1,2,2l);
		matrix.assign(1,3,2l);
		matrix.assign(1,7,2l);
		matrix.assign(1,8,2l);
		matrix.assign(1,10,2l);
		// C.
		for (int i=0;i<11;++i) matrix.add(2,i,3l*matrix.get(0,i));
		matrix.add(2,3,2l);
		matrix.add(2,4,2l);
		matrix.add(2,8,2l);
		matrix.add(2,9,2l);
		// D.
		matrix.assign(3,2,2l);
		matrix.assign(3,7,2l);
		matrix.assign(3,5,2l);
		// E.
		for (int i=0;i<11;++i) matrix.add(4,i,3l*matrix.get(0,i));
		matrix.add(4,3,2l);
		matrix.add(4,6,2l);
		// F.
		matrix.assign(5,2,2l);
		// G.
		for (int i=0;i<11;++i) matrix.add(6,i,3l*matrix.get(0,i));
		// E-G (-1).
		for (int i=7;i<=9;++i) matrix.assign(i,i-3,1l);
		// G (-2).
		matrix.assign(10,9,1l);
		long[] initialVector=new long[] {a3,b3,c3,d3,e3,f3,g3,e2,f2,g2,g1};
		LongMatrix expMatrix=matrix.pow(EXP-3,MOD);
		long[] lastVector=expMatrix.multiply(initialVector,MOD);
		long result=(lastVector[0]+lastVector[1])%MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
