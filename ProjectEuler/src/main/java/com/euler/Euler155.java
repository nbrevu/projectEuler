package com.euler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.Rational;
import com.euler.common.Timing;
import com.google.common.collect.ImmutableSet;

public class Euler155 {
	private final static int N=18;
	
	private static class CircuitHolder	{
		private final List<Set<Rational>> holder;
		private final Set<Rational> found;
		private final int N;
		public CircuitHolder(int N)	{
			holder=new ArrayList<>(N);
			found=new HashSet<>();
			this.N=N;
		}
		public void calculate()	{
			holder.add(ImmutableSet.of(Rational.ONE));
			found.add(Rational.ONE);
			for (int k=1;k<N;++k)	{
				Set<Rational> currentSet=new HashSet<>();
				int half=(k+1)/2;	// For k=1 we need half=1; for k=2 we need half=1; for k=3 we need half=2. And so on.
				for (int i=0;i<half;++i)	{
					int j=k-1-i;
					for (Rational r1:holder.get(i)) for (Rational r2:holder.get(j))	{
						Rational s=r1.sum(r2);
						Rational p=r1.harmonicSum(r2);
						if (!found.contains(s))	{
							found.add(s);
							currentSet.add(s);
						}
						if (!found.contains(p))	{
							found.add(p);
							currentSet.add(p);
						}
					}
				}
				holder.add(currentSet);
			}
		}
		public int size()	{
			return found.size();
		}
	}
	
	private static long solve()	{
		CircuitHolder holder=new CircuitHolder(N);
		holder.calculate();
		return holder.size();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler155::solve);
	}
}