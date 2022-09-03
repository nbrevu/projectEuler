package com.euler;

import java.util.List;

import com.euler.common.Primes;

public class Euler569_2 {
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
	
	public static void main(String[] args)	{
		Peak[] peaks=createPeaks(AMOUNT,PRIMES);
		long visibleSum=0;
		for (int i=1;i<AMOUNT;++i)	{
			visibleSum+=getVisiblePeaksFrom(peaks,i);
			if ((i%100000)==0) System.out.println(""+i+"...");
		}
		System.out.println(visibleSum);
	}
}
