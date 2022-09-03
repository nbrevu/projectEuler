package com.euler;

import com.google.common.math.LongMath;

public class Euler633 {
	// Next stop: 143.
	public static void main(String[] args)	{
		for (int i=143;;++i)	{
			long limit=LongMath.pow(10l,15)*i;
			long[] counters=Euler632_2.getSquareFactors(limit);
			double quotient=(double)counters[7]/(double)limit;
			System.out.println(limit+": "+quotient);
		}
	}
}
