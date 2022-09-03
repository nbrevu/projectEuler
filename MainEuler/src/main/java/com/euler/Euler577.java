package com.euler;

import java.math.RoundingMode;

import com.google.common.math.IntMath;

public class Euler577 {
	private final static int LIMIT=12345;
	/*
	1 3 6 12 21 33 51 75 105 145 195 255
	Diff:
	1 2 3 6 9 12 18 24 30 40 50 60
	=
	1*(1 2 3)
	3*(2 3 4)
	6*(3 4 5)
	10*(4 5 6)
	
	This CAN be done in constant time. The differences are cubic, therefore H is a four grade polynomial and the result is a
	fifth grade polynomial. Of course, someone in the PE thread has posted the formula.
	 */
	
	private static long getTriangular(long in)	{
		return ((in+1)*in)/2;
	}
	
	private static long[] getDiffs(int size)	{
		int triples=IntMath.divide(size,3,RoundingMode.UP);
		long[] result=new long[3*triples];
		for (int i=0;i<triples;++i)	{
			int index=3*i;
			long factor=getTriangular(i+1);
			long num=factor*(i+1);
			result[index]=num;
			num+=factor;
			result[index+1]=num;
			result[index+2]=num+factor;
		}
		return result;
	}
	
	private static long[] accumulate(long[] in)	{
		int N=in.length;
		long[] result=new long[N];
		result[0]=in[0];
		for (int i=1;i<N;++i) result[i]=result[i-1]+in[i];
		return result;
	}
	
	private static long accumulateTotal(int in)	{
		long[] diffs=getDiffs(in);
		long[] H=accumulate(diffs);
		long[] result=accumulate(H);
		return result[in-1];
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=accumulateTotal(LIMIT-2);
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
