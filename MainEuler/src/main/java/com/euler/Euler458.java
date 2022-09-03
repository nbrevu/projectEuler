package com.euler;

import com.euler.common.LongMatrix;
import com.google.common.math.LongMath;

public class Euler458 {
	// As I expected, this doesn't work... so do we need a 5040x5040 matrix? This looks daunting.
	private final static long LIMIT=LongMath.pow(10l,12);
	private final static long MOD=LongMath.pow(10l,9);
	
	private static LongMatrix getMatrix()	{
		/*
		 * This is a transition matrix between seven states. The first 6 are "strings without PROJECT permutations, where the last 6 characters
		 * include (k) distinct ones". The last one is "strings that have a PROJECT permutation somewhere".
		 * 
		 * The vector representing the strings of length 1 is [7,0,0,0,0,0,0]. The matrix is raised to the (LIMIT-1) power, then multiplied by
		 * the vector. We then sum the first six states of the result. 
		 */
		LongMatrix result=new LongMatrix(7);
		for (int i=0;i<6;++i)	{
			for (int j=0;j<=i;++j) result.assign(i,j,1l);
			result.assign(i,i+1,6-i);
		}
		result.assign(6,6,7l);
		return result;
	}
	
	public static void main(String[] args)	{
		LongMatrix base=getMatrix();
		LongMatrix pow=base.pow(LIMIT-1,MOD);
		// Simulate the product with [7,0,0,0,0,0,0]': we add the first column (minus the last element), then multiply by 7.
		System.out.println(pow);
		long columnSum=0;
		for (int i=0;i<6;++i) columnSum+=pow.get(i,0);
		long result=(columnSum*7)%MOD;
		System.out.println(result);
	}
}
