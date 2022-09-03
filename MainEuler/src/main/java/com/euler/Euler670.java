package com.euler;

import com.euler.common.LongMatrix;

public class Euler670 {
	//private final static long N=LongMath.pow(10l,16);
	private final static long MOD=1_000_004_321l;
	
	public static void main(String[] args)	{
		long a0=1l;
		long a1=4*a0;
		long b1=12*a0;
		long c1=12*a0;
		long d1=12*a0;
		long e1=12*a0;
		long f1=12*a0;
		long a2=3*a1+2*b1;
		long b2=6*a1+60*a0;
		long c2=6*a1+12*a0+2*d1;
		long d2=6*a1+12*a0+2*c1;
		long e2=6*a1+2*d1;
		long f2=6*a1+2*c1;
		long a3=3*a2+2*b2;
		long b3=6*a2+6*a1+204*a0+4*c1+4*d1;
		long c3=6*a2+6*a1+2*d2+2*f1;
		long d3=6*a2+6*a1+2*c2+2*e1;
		long e3=6*a2+2*d2;
		long f3=6*a2+2*c2;
		LongMatrix baseMatrix=new LongMatrix(18);
		// A(x)=3A(x-1)+2B(x-2).
		baseMatrix.assign(0,0,3l);
		baseMatrix.assign(0,7,2l);
		// B(x)=6A(x-1)+30A(x-2)+102A(x-3)+4C(x-2)+4C(x-3)+4D(x-2)+4D(x-3)+4E(x-3)+4F(x-3).
		baseMatrix.assign(1,0,6l);
		baseMatrix.assign(1,6,30l);
		baseMatrix.assign(1,12,102l);
		baseMatrix.assign(1,8,4l);
		baseMatrix.assign(1,14,4l);
		baseMatrix.assign(1,9,4l);
		baseMatrix.assign(1,15,4l);
		baseMatrix.assign(1,16,4l);
		baseMatrix.assign(1,17,4l);
		// C(x)=6A(x-1)+6A(x-2)+2D(x-1)+2F(x-2).
		baseMatrix.assign(2,0,6l);
		baseMatrix.assign(2,6,6l);
		baseMatrix.assign(2,3,2l);
		baseMatrix.assign(2,11,2l);
		// D(x)=6A(x-1)+6A(x-2)+2C(x-1)+2E(x-2).
		baseMatrix.assign(3,0,6l);
		baseMatrix.assign(3,6,6l);
		baseMatrix.assign(3,2,2l);
		baseMatrix.assign(3,10,2l);
		// E(x)=6A(x-1)+2D(x-1).
		baseMatrix.assign(4,0,6l);
		baseMatrix.assign(4,3,2l);
		// F(x)=6A(x-1)+2C(x-1).
		baseMatrix.assign(5,0,6l);
		baseMatrix.assign(5,2,2l);
		// Displaced identity, Var(x-1)=Var(x-1) and Var(x-2)=Var(x-2).
		for (int i=6;i<18;++i) baseMatrix.assign(i,i-6,1l);
		//long exp=2l;
		LongMatrix matrixPower=baseMatrix.pow(2l);
		long[] vector=new long[] {a3,b3,c3,d3,e3,f3,a2,b2,c2,d2,e2,f2,a1,b1,c1,d1,e1,f1};
		long[] result=matrixPower.multiply(vector,MOD);
		System.out.println(result[0]+result[1]);
	}
}
