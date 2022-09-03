package com.euler;

import java.util.Arrays;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler539_3 {
	public final static long N=3*LongMath.pow(2,15);
	
	private static class Series	{
		// xk = a*k + b, from k=1 to k=n.
		private final long a;
		private final long b;
		private final long n;
		private final boolean reducesFromLeft;
		public Series(long n)	{
			a=1;
			b=0;
			this.n=n;
			reducesFromLeft=true;
		}
		private Series(long a,long b,long n,boolean reducesFromLeft)	{
			this.a=a;
			this.b=b;
			this.n=n;
			this.reducesFromLeft=reducesFromLeft;
		}
		public Series child()	{
			if (n<=1) throw new IllegalStateException("Not enough elements.");
			boolean removeOdds=reducesFromLeft||((n%2)==1);
			long newA=2*a;
			long newN=n/2;
			long newB=removeOdds?b:(b-a);
			return new Series(newA,newB,newN,!reducesFromLeft);
		}
		public long reduce()	{
			if (n==1) return a+b;
			else return child().reduce();
		}
	}
	
	private static class CycledInterval	{
		public final int start;
		public final int end;
		public final int howManyCycles;
		public final long[] cycle;
		public CycledInterval(int start,int end)	{
			this.start=start;
			this.end=end;
			long[] allValues=new long[end-start];
			for (int i=start;i<end;++i) allValues[i-start]=new Series(i).reduce();
			int size=end-start;
			int hmc=0;
			long[] c=null;
			for (int s=1;s<end;++s)	{
				// Although this method is generic, in practice I believe I will only use it for powers of 2.
				if ((size%s)!=0) continue;
				boolean isCycleValid=true;
				for (int i=s;i<size;++i) if (allValues[i]!=allValues[i-s])	{
					isCycleValid=false;
					break;
				}
				if (isCycleValid)	{
					hmc=size/s;
					c=Arrays.copyOf(allValues,s);
					break;
				}
			}
			if (c==null) throw new RuntimeException("Couldn't find cycles between "+start+" and "+end+" :'(.");
			howManyCycles=hmc;
			cycle=c;
		}
		@Override
		public String toString()	{
			return String.format("From %d to %d there is a series of %d cycles of length %d: %s.",start,end,howManyCycles,cycle.length,Arrays.toString(cycle));
		}
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=7;++i)	{
			System.out.println(new CycledInterval(IntMath.pow(2,2*i+1),3*IntMath.pow(2,2*i+1)));
		}
	}
}
