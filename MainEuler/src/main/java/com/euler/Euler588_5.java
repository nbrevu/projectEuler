package com.euler;

import java.util.HashSet;
import java.util.Set;

public class Euler588_5 {
	private static class SetOfOnes	{
		private Set<Long> positions;
		public SetOfOnes(long firstElements)	{
			positions=new HashSet<>();
			for (long i=0;i<firstElements;++i) positions.add(i);
		}
		private SetOfOnes(Set<Long> positions)	{
			this.positions=positions;
		}
		public int getOddCoefficients()	{
			return positions.size();
		}
		public SetOfOnes fifthPower()	{
			Set<Long> result=new HashSet<>();
			for (long p1:positions)	{
				toggle(result,p1*5);
				long p14=p1*4;
				for (long p2:positions) if (p1!=p2) toggle(result,p14+p2);
			}
			return new SetOfOnes(result);
		}
		private static void toggle(Set<Long> set,Long position)	{
			if (set.contains(position)) set.remove(position);
			else set.add(position);
		}
	}
	
	// VERY interesting finding: apparently, for non-powers of 2, odd(X) = odd(2*X), and even better,
	// odd(X) = odd((2^N)*X), for each N. This means that we will only need powers of 5.
	private final static int EXPONENT=18;
	
	public static void main(String[] args)	{
		SetOfOnes polynomial=new SetOfOnes(5);
		long sum=0;
		for (int i=1;i<=EXPONENT;++i)	{
			polynomial=polynomial.fifthPower();
			long oddCoeffs=polynomial.getOddCoefficients();
			System.out.println("For 5^"+i+", there are "+oddCoeffs+" odd coefficients.");
			sum+=oddCoeffs;
		}
		System.out.println(sum);
	}
}
