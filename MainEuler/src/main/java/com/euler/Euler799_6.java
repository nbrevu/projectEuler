package com.euler;

import java.util.Arrays;

public class Euler799_6 {
	private static long pentagonal(long n)	{
		long p=n*(3*n-1);
		return p/2;
	}
	
	public static void main(String[] args)	{
		int N=27042068;
		long[] pentas=new long[1+N];
		for (int i=1;i<=N;++i) pentas[i]=pentagonal(i);
		double s2=Math.sqrt(2d);
		long me=pentas[N];
		int minTerm=Math.max((int)Math.floor(N/s2),1);
		int ways=0;
		for (int i=minTerm;i<N;++i)	{
			long other=pentas[i];
			int pos=Arrays.binarySearch(pentas,1,i,me-other);
			if ((pos>0)&&(pos<=i))	{
				++ways;
				if (pos==i) System.out.println("\tWIIIIIIIIIII");
				System.out.println(String.format("P(%d)+P(%d)=P(%d) => %d+%d=%d.",pos,i,N,pentas[pos],pentas[i],me));
			}
		}
		System.out.println(ways);
	}
}
