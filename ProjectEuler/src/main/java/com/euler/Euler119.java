package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.Timing;

public class Euler119 {
	private final static int N=30;
	
	private final static int MAX_DIGITS=18;	// In practice, this is a long.
	private final static int MAX_CANDIDATE=9*MAX_DIGITS;
	
	private static class Power	{
		public final int base;
		public final long number;
		public Power(int base,long number)	{
			this.base=base;
			this.number=number;
		}
		public boolean isSumVerified()	{
			int result=base;
			long n=number;
			while (n>0)	{
				result-=(int)(n%10);
				if (result<0) return false;
				n/=10;
			}
			return result==0;
		}
	}
	
	private static void addToQueues(NavigableMap<Long,List<Power>> queues,Power pow)	{
		List<Power> list=queues.computeIfAbsent(pow.number,(Long unused)->new ArrayList<>());
		list.add(pow);
	}
	
	private static long solve()	{
		NavigableMap<Long,List<Power>> queues=new TreeMap<>();
		for (int i=2;i<10;++i) addToQueues(queues,new Power(i,i*i));
		for (int i=10;i<=MAX_CANDIDATE;++i) addToQueues(queues,new Power(i,i));
		int found=0;
		for (;;) for (Power pow:queues.pollFirstEntry().getValue())	{
			if (pow.isSumVerified())	{
				++found;
				if (found>=N) return pow.number;
			}
			Power nextPow=new Power(pow.base,pow.base*pow.number);
			addToQueues(queues,nextPow);
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler119::solve);
	}
}
