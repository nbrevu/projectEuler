package com.euler;

import java.util.Arrays;

public class Euler799_2 {
	private static long pentagonal(long n)	{
		long p=n*(3*n-1);
		return p/2;
	}
	
	public static void main(String[] args)	{
		int N=10_000_000;
		long[] pentas=new long[1+N];
		int maxWays=0;
		double s2=Math.sqrt(2d);
		for (int i=1;i<=N;++i)	{
			long me=pentagonal(i);
			pentas[i]=me;
			int ways=0;
			int minTerm=Math.max((int)Math.floor(i/s2),1);
			for (int j=minTerm;j<i;++j)	{
				long other=pentas[j];
				int pos=Arrays.binarySearch(pentas,1,j,me-other);
				if ((pos>0)&&(pos<=j)) ++ways;
			}
			if (ways>maxWays)	{
				System.out.println(String.format("\tP(%d) can be expressed in %d ways.",i,ways));
				System.out.println("\tThis is the maximum so far!!!!!");
				if (ways>100)	{
					System.out.println(me);
					return;
				}
				maxWays=ways;
			}
		}
	}
}
