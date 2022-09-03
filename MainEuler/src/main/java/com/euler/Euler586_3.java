package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.LongIntMap;

public class Euler586_3 {
	private final static int N=IntMath.pow(10,5);
	
	private static class IntPair	{
		public final int a;
		public final int b;
		public IntPair(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		@Override
		public String toString()	{
			return "<"+a+","+b+">";
		}
	}
	
	private static class BinaryFormInformation	{
		private final LongIntMap decomp;
		private final List<IntPair> counters;
		public BinaryFormInformation(LongIntMap decomp,List<IntPair> counters)	{
			this.decomp=decomp;
			this.counters=counters;
		}
	}
	private static class BinaryFormInformationGenerator	{
		private final List<List<IntPair>> counters;
		private final int[] lastPrimeSieve;
		public BinaryFormInformationGenerator(int limit)	{
			lastPrimeSieve=Primes.lastPrimeSieve(limit);
			counters=new ArrayList<>(1+limit);
			for (int i=0;i<=limit;++i) counters.add(new ArrayList<>());
			for (int a=1;;++a)	{
				int a2=a*a;
				if (a2>limit) break;
				for (int b=1;b<a;++b)	{
					int f=a2+b*(3*a+b);
					if (f>limit) break;
					counters.get(f).add(new IntPair(a,b));
				}
			}
		}
		public BinaryFormInformation getFor(int n)	{
			return new BinaryFormInformation(DivisorHolder.getFromFirstPrimes(n,lastPrimeSieve).getFactorMap(),counters.get(n));
		}
	}
	
	public static void main(String[] args)	{
		BinaryFormInformationGenerator gen=new BinaryFormInformationGenerator(N);
		int counter=0;
		for (int i=2;i<=N;++i)	{
			BinaryFormInformation info=gen.getFor(i);
			//if (info.counters.size()<=1) continue;
			System.out.println(i+":");
			System.out.println("\tPrime decomposition: "+info.decomp+".");
			System.out.println("\tValid results="+info.counters.size()+": "+info.counters+".");
			if (info.counters.size()==4) ++counter;
		}
		System.out.println(counter);
	}
}
