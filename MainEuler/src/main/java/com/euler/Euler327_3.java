package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler327_3 {
	private static long getM(int c,int r)	{
		if (r<c) return r+1;
		else if (r==c) return 3+r;
		long prev=getM(c,r-1);
		long prevReduced=prev-(c-1);
		long q=LongMath.divide(prevReduced,c-2,RoundingMode.UP);
		return prevReduced+2*q+c;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		for (int c=3;c<=40;++c) result+=getM(c,30);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
