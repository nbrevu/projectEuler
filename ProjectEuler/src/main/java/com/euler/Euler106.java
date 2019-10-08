package com.euler;

import com.euler.common.CombinationIterator;
import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.Timing;

public class Euler106 {
	private final static int N=12;
	
	private static int[] generateOpposite(int[] in)	{
		// Explicit generation could be avoided, but this is more understandable, I guess.
		int[] result=new int[in.length];
		int currentReadIndex=0;
		int currentWriteIndex=0;
		for (int i=0;i<2*in.length;++i) if ((currentReadIndex<in.length)&&(in[currentReadIndex]==i)) ++currentReadIndex;
		else	{
			result[currentWriteIndex]=i;
			++currentWriteIndex;
		}
		return result;
	}
	
	private static boolean isOrdered(int[] in,int[] opposite)	{
		for (int i=0;i<in.length;++i) if (in[i]>opposite[i]) return false;
		return true;
	}
	
	private static long solve()	{
		{
			CombinatorialNumberCache cache=new CombinatorialNumberCache(N);
			for (int n=1;n<=20;++n)	{
				long ms=0;
				for (int k=2;k<=n/2;++k) ms+=cache.get(n,2*k)*cache.get(2*k-1,k+1);
				System.out.println(n+": "+ms+".");
			}
		}
		
		long result=0;
		int N2=N/2;
		CombinatorialNumberCache cache=new CombinatorialNumberCache(N);
		for (int i=1;i<=N2;++i)	{
			CombinationIterator iterator=new CombinationIterator(i,2*i,false,true);
			int counter=0;
			for (int[] comb:iterator)	{
				if (comb[0]>0) break;
				int[] opposite=generateOpposite(comb);
				if (!isOrdered(comb,opposite)) ++counter;
			}
			result+=counter*cache.get(N,2*i);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler106::solve);
	}
}
