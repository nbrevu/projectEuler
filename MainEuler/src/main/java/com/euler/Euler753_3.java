package com.euler;

public class Euler753_3 {
	public static void main(String[] args)	{
		// I can't use powers of 2: for p=31 my hypothesis doesn't work...
		int p=19;
		for (int i=1;i<p;++i)	{
			long c=i*i*i;
			System.out.println(String.format("%d -> %d.",i,c%p));
			// 37: we expect 12 different residues. These are: 1, 6, 8, 10, 11, 14, 23, 26, 27, 29, 31, 36
			/*
			 * Possible sums are:
			 * 	1+10=11
			 *  1+26=27
			 *  6+8=14
			 *  6+23=29
			 *  8+23=31
			 *  10+26=36
			 *  11+27=1
			 *  11+36=10
			 *  14+29=6
			 *  14+31=8
			 *  27+36=26
			 *  29+31=23
			 *  
			 *  Each residue appears exactly two times. Let n=(p-1)/3 be the amount of residues. There must be exactly n combinations,
			 *  each one of which can be fulfilled in 27 ways, so there would be 27n combinations.
			 *  
			 *  Hypothesis: each residue appears the same amount of times. This must be an even number, X. So, in these cases (assuming that 2 is
			 *  not a residue), there would be n*(X/2)*27 sums.
			 *  
			 *  For cases where 2 is a residue, there will be cases of the form A+A=B.
			 */
		}
	}
}
