package com.euler;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;

import com.euler.common.Primes;

public class Euler789_3 {
	private static class IntPair	{
		public final int a;
		public final int b;
		public IntPair(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		@Override
		public String toString()	{
			return String.format("(%d,%d)",a,b);
		}
	}
	
	private static class PairFinder	{
		private final int prime;
		public PairFinder(int prime)	{
			this.prime=prime;
		}
		public void display(Deque<IntPair> pairs)	{
			int sumCost=0;
			long prodCost=1l;
			for (IntPair p:pairs)	{
				int cost=(p.a*p.b)%prime;
				sumCost+=cost;
				prodCost*=cost;
			}
			if (sumCost<prime) System.out.println(String.format("Encontrado caso chachiguay para p=%d (%s): suma=%d, producto=%d.",prime,pairs,sumCost,prodCost));
		}
		public void findAll()	{
			Deque<IntPair> pairs=new ArrayDeque<>();
			BitSet assigned=new BitSet();
			findAllRecursive(pairs,assigned);
		}
		private void findAllRecursive(Deque<IntPair> pairs,BitSet assigned)	{
			int firstPart=assigned.nextClearBit(0);
			assigned.set(firstPart);
			for (int secondPart=assigned.nextClearBit(1+firstPart);(secondPart>=0)&&(secondPart<prime-1);secondPart=assigned.nextClearBit(1+secondPart))	{
				assigned.set(secondPart);
				pairs.addLast(new IntPair(1+firstPart,1+secondPart));
				if (assigned.cardinality()<prime-1) findAllRecursive(pairs,assigned);
				else display(pairs);
				pairs.removeLast();
				assigned.clear(secondPart);
			}
			assigned.clear(firstPart);
		}
	}
	
	public static void main(String[] args)	{
		boolean[] composites=Primes.sieve(100);
		for (int i=29;i<=100;i+=2) if (!composites[i]) new PairFinder(i).findAll();
	}
}
