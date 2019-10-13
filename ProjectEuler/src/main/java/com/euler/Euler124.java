package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler124 {
	private final static int LIMIT=IntMath.pow(10,5);
	private final static int POSITION=IntMath.pow(10,4);
	
	private static class Radical implements Comparable<Radical>	{
		public final long number;
		public final long radical;
		public Radical(long number,long radical)	{
			this.number=number;
			this.radical=radical;
		}
		@Override
		public int compareTo(Radical o) {
			int result=Long.compare(radical,o.radical);
			return (result!=0)?result:Long.compare(number,o.number);
		}
		@Override
		public String toString()	{
			return "("+number+","+radical+")";
		}
	}
	
	private static long solve()	{
		int[] firstPrimes=Primes.firstPrimeSieve(1+LIMIT);
		List<Radical> radicals=new ArrayList<>(1+LIMIT);
		radicals.add(new Radical(0,0));
		radicals.add(new Radical(1,1));
		for (int i=2;i<=LIMIT;++i)	{
			int p=firstPrimes[i];
			long radical;
			if (p==0) radical=i;
			else	{
				Radical prev=radicals.get(i/p);
				radical=((prev.radical%p)==0)?prev.radical:(p*prev.radical);
			}
			radicals.add(new Radical(i,radical));
		}
		radicals.sort(null);
		return radicals.get(POSITION).number;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler124::solve);
	}
}
