package com.euler;

public class Euler799 {
	private static long pentagonal(long n)	{
		long p=n*(3*n-1);
		return p/2;
	}
	
	public static void main(String[] args)	{
		int N=50000;
		long[] pentas=new long[1+N];
		int maxWays=0;
		for (int i=1;i<=N;++i)	{
			pentas[i]=pentagonal(i);
			int ways=0;
			for (int j=1;j<i;++j) for (int k=j;k<i;++k)	{
				long pentaSum=pentas[j]+pentas[k];
				if (pentaSum<pentas[i]) continue;
				else if (pentaSum==pentas[i])	{
					// System.out.println(String.format("OH NEIN!!!!! P(%d)=P(%d)+P(%d).",i,k,j));
					++ways;
				}
				break;
			}
			// if (ways>0) System.out.println(String.format("\tP(%d) can be expressed in %d ways.",i,ways));
			if (ways>maxWays)	{
				System.out.println(String.format("\tP(%d) can be expressed in %d ways.",i,ways));
				System.out.println("\tThis is the maximum so far!!!!!");
				maxWays=ways;
			}
		}
	}
}
