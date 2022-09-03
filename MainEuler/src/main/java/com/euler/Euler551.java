package com.euler;

import java.util.HashMap;
import java.util.Map;

import com.google.common.math.LongMath;

public class Euler551 {
	// No, this doesn't work :(.
	private final static long LIMIT=LongMath.pow(10l,6);

	private final static long MOD=LongMath.pow(10l,6);
	
	private static long getDigitsSum(long n)	{
		long result=0;
		while (n>0)	{
			result+=n%10;
			n/=10;
		}
		return result;
	}
	
	private static class TermSummary	{
		public final long mod;
		public final long digitsSum;
		private final int hashCode;
		public TermSummary(long in)	{
			mod=in%MOD;
			digitsSum=getDigitsSum(in);
			hashCode=Long.hashCode(mod)+Long.hashCode(digitsSum);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			TermSummary o=(TermSummary)other;
			return (mod==o.mod)&&(digitsSum==o.digitsSum);
		}
		public boolean isDangerous()	{
			return (mod/(MOD/10))==9l;
		}
	}
	
	private static class TermAndPosition	{
		public final long term;
		public final long position;
		public TermAndPosition(long term,long position)	{
			this.term=term;
			this.position=position;
		}
	}
	
	public static void main(String[] args)	{
		long n=2;
		long aN=2;
		Map<TermSummary,TermAndPosition> memory=new HashMap<>();
		for (;n<LIMIT;)	{
			TermSummary term=new TermSummary(aN);
			TermAndPosition lastOccurrence=memory.get(term);
			if ((lastOccurrence!=null)&&!term.isDangerous())	{
				System.out.println("WIIIIIIIIIIIIIIIIIIIII");
				long posDifference=n-lastOccurrence.position;
				if (n+posDifference<=LIMIT)	{
					long termDifference=aN-lastOccurrence.term;
					aN+=termDifference;
					n+=posDifference;
					continue;
				}
			}				
			aN+=term.digitsSum;
			++n;
			lastOccurrence=new TermAndPosition(aN,n);
			memory.put(term,lastOccurrence);
		}
		System.out.println("n="+n+"; a_n="+aN+".");
	}
}
