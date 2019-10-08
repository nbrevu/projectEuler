package com.euler;

import com.euler.common.Timing;
import com.google.common.math.LongMath;

public class Euler104 {
	private final static long MOD1=LongMath.pow(10l,9);
	private final static long LIMIT=LongMath.pow(10l,18);
	
	private static class FibonacciAccumulator	{
		private final long firstDigits;
		private final long lastDigits;
		private final int offset;
		public FibonacciAccumulator()	{
			firstDigits=1l;
			lastDigits=1l;
			offset=0;
		}
		private FibonacciAccumulator(long firstDigits,long lastDigits,int offset)	{
			this.firstDigits=firstDigits;
			this.lastDigits=lastDigits;
			this.offset=offset;
		}
		private FibonacciAccumulator add(FibonacciAccumulator other)	{
			long first1=firstDigits;
			long first2=other.firstDigits;
			int newOffset=offset;
			if (offset>other.offset) first2/=10;
			long newFirstDigits=first1+first2;
			if (newFirstDigits>LIMIT)	{
				newFirstDigits/=10;
				++newOffset;
			}
			long newLastDigits=(lastDigits+other.lastDigits)%MOD1;
			return new FibonacciAccumulator(newFirstDigits,newLastDigits,newOffset);
		}
		public boolean isPandigital()	{
			return isPandigital(lastDigits)&&isPandigital(trim(firstDigits));
		}
		private static boolean isPandigital(long in)	{
			boolean[] values=new boolean[10];
			int counter=0;
			while (in>0)	{
				int d=(int)(in%10);
				if ((d==0)||(values[d])) return false;
				values[d]=true;
				in/=10;
				++counter;
			}
			return counter==9;
		}
		private static long trim(long in)	{
			while (in>MOD1) in/=10l;
			return in;
		}
	}
	
	private static long solve()	{
		FibonacciAccumulator prev=new FibonacciAccumulator();
		FibonacciAccumulator curr=new FibonacciAccumulator();
		for (int result=3;;++result)	{
			FibonacciAccumulator next=curr.add(prev);
			if (next.isPandigital()) return result;
			prev=curr;
			curr=next;
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler104::solve);
	}
}
