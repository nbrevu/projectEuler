package com.euler;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.math.LongMath;

public class Euler370_2 {
	private final static long LIMIT=25*LongMath.pow(10l,12);
	
	private static class Fraction	{
		public final int n;
		public final int d;
		public Fraction(int n,int d)	{
			this.n=n;
			this.d=d;
		}
	}
	private static class Interval	{
		public final Fraction lower;
		public final Fraction middle;
		public final Fraction upper;
		public final long middleId;
		public Interval(Fraction lower,Fraction middle,Fraction upper,long middleId)	{
			this.lower=lower;
			this.middle=middle;
			this.upper=upper;
			this.middleId=middleId;
		}
	}
	private static class FractionQueue	{
		private final long limit;
		private final Deque<Interval> pending;
		public FractionQueue(long limit)	{
			this.limit=limit;
			pending=new ArrayDeque<>(100_000_000);
			Fraction f0=new Fraction(0,1);
			Fraction f1=new Fraction(1,1);
			Fraction f12=new Fraction(1,2);
			long value=1+3*2*(1+2);
			pending.add(new Interval(f0,f12,f1,value));
		}
		public boolean isEmpty()	{
			return pending.isEmpty();
		}
		public Interval poll()	{
			return pending.poll();
		}
		public void addChildren(Interval interval)	{
			addIfValid(interval.lower,interval.middle);
			addIfValid(interval.middle,interval.upper);
		}
		private void addIfValid(Fraction lower,Fraction upper)	{
			int n=lower.n+upper.n;
			int d=lower.d+upper.d;
			long ln=n;
			long ld=d;
			long nd=ln+ld;
			if (ld*ld<ln*nd)	{
				long middleId=ln*ln+3*ld*nd;
				if (middleId<=limit) pending.add(new Interval(lower,new Fraction(n,d),upper,middleId));
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=LIMIT/3;	// Special case for n/d=0/1.
		FractionQueue queue=new FractionQueue(LIMIT);
		while (!queue.isEmpty())	{
			Interval interval=queue.poll();
			result+=LIMIT/interval.middleId;
			queue.addChildren(interval);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);	// Oooooh, es funktioniert nicht.
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
