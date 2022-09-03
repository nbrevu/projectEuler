package com.euler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.Primes;

public class Euler569_3 {
	private final static int AMOUNT=2500000;
	private final static List<Long> PRIMES=Primes.listLongPrimes(40*AMOUNT);
	
	private static class Peak	{
		public final long x;
		public final long y;
		public double maxSlope;
		public Peak(long x,long y)	{
			this.x=x;
			this.y=y;
			maxSlope=0.0;
		}
	}
	
	private static Peak[] createPeaks(int amount,List<Long> primes)	{
		assert primes.size()>=2*amount-1;
		Peak[] result=new Peak[amount];
		result[0]=new Peak(primes.get(0),primes.get(0));
		long curX=result[0].x;
		long curY=result[0].y;
		for (int i=1;i<amount;++i)	{
			long p1=primes.get(2*i-1);
			long p2=primes.get(2*i);
			curX+=p2+p1;
			curY+=p2-p1;
			result[i]=new Peak(curX,curY);
		}
		return result;
	}
	
	private static double getSlope(Peak start,Peak end)	{
		long currDiffX=start.x-end.x;
		long currDiffY=start.y-end.y;
		return ((double)currDiffY)/((double)currDiffX);
	}
	
	private static long getVisiblePeaksFrom(Peak[] allPeaks,int index)	{
		// The first peak is always visible.
		long sum=1;
		Peak curr=allPeaks[index];
		int lastIndex=index-1;
		Peak last=allPeaks[lastIndex];
		double bestSlope=getSlope(curr,last);
		for (;;)	{
			if (last.maxSlope>bestSlope) break;
			--lastIndex;
			if (lastIndex<0) break;
			last=allPeaks[lastIndex];
			double slope=getSlope(curr,last);
			if (slope<bestSlope)	{
				++sum;
				bestSlope=slope;
			}
		}
		curr.maxSlope=bestSlope;
		return sum;
	}
	
	private static void increase(Map<Long,Integer> map,long key)	{
		Integer value=map.get(key);
		int newValue=1+((value==null)?0:value.intValue());
		map.put(key,newValue);
	}
	
	public static void main(String[] args)	{
		Peak[] peaks=createPeaks(AMOUNT,PRIMES);
		long visibleSum=0;
		Map<Long,Integer> stats=new HashMap<>();
		for (int i=1;i<AMOUNT;++i)	{
			long visible=getVisiblePeaksFrom(peaks,i);
			increase(stats,visible);
			visibleSum+=visible;
		}
		System.out.println(visibleSum);
		for (Map.Entry<Long,Integer> entry:stats.entrySet()) System.out.println(""+entry.getKey()+" => "+entry.getValue());
	}
}
