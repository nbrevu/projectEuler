package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;

public class Euler748_2 {
	/*
	 * OK. This code confirms what I suspect:
	 * - Only products of 4k+1-type primes are needed.
	 * - Generally speaking, iterating up to sqrt(LIMIT) is the qay to go.
	 * - I still need a way to efficiently calculate the ways to express x^2+y^2=p or p^2 with p prime (I can use the known formula for the other cases).
	 * 
	 * Let p=5. Obviously there is a single form, (1,2).
	 * Can it be used to generate the case for 25?
	 * a=1,b=2,c=1,d=2: (ac+bd)=5,(ad-bc)=0 -> nicht möglich. There will always be one like that for every prime.
	 * a=1,b=2,c=2,d=1: (ac+bd)=4,(ad-bc)=-3 -> ok, take (4,3).
	 * Can these be combined with (2,3), which is the result for 13?
	 * a=3,b=4,c=2,d=3: (ac+bd)=18,(ad-bc)=1 -> great :).
	 * a=3,b=4,c=3,d=2: (ac+bd)=17,(ad-bc)=-6 -> sure, let's go for 17 and 6.
	 * Definitely we only need an algorithm for the prime case. Yay?
	 */
	private static class IntPair	{
		public final int a;
		public final int b;
		public IntPair(int a,int b)	{
			this.a=a;
			this.b=b;
		}
		public boolean areCoprime()	{
			return EulerUtils.areCoprime(a,b);
		}
	}
	private static class SquareSumFinder	{
		private final long[] squares;
		public SquareSumFinder(long limit)	{
			squares=new long[(int)LongMath.sqrt(limit,RoundingMode.UP)];
			for (int i=0;i<squares.length;++i) squares[i]=i*(long)i;
		}
		public List<IntPair> getSquareSums(long limit)	{
			List<IntPair> result=new ArrayList<>();
			for (int i=1;i<squares.length;++i)	{
				long sq=squares[i];
				long diff=limit-sq;
				if (sq>diff) break;
				int pos=Arrays.binarySearch(squares,diff);
				if (pos>0) result.add(new IntPair(i,pos));
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		int limit=10000;
		int[] lastPrimes=Primes.lastPrimeSieve(limit);
		SquareSumFinder finder=new SquareSumFinder(13l*limit*limit);
		for (int i=1;i<=limit;++i)	{
			List<IntPair> sums=finder.getSquareSums(13l*i*i);
			List<IntPair> interestingSums=sums.stream().filter(IntPair::areCoprime).collect(Collectors.toList());
			if (!interestingSums.isEmpty())	{
				DivisorHolder decomposition=(i==1)?new DivisorHolder():DivisorHolder.getFromFirstPrimes(i,lastPrimes);
				for (LongCursor cursor=decomposition.getFactorMap().keySet().cursor();cursor.moveNext();) if (((cursor.elem()-1)%4)!=0) throw new RuntimeException("Mierda, esto no me lo esperaba.");
				int[] exponents=decomposition.getFactorMap().values().toIntArray();
				Arrays.sort(exponents);
				System.out.println(String.format("%d (exponentes %s): %d casos.",i,Arrays.toString(exponents),interestingSums.size()));
				long sum=0;
				for (IntPair p:interestingSums)	{
					/*
					 * a^2 + b^2 = 13*i^2.
					 * Dividimos por (a*b*i)^2:
					 * 1/(b*i)^2 + 1/(a*i)^2 = 13/(a*b)^2.
					 * Invertimos x e y para que sea x<y.
					 */
					int x=p.a*i;
					int y=p.b*i;
					int z=p.a*p.b;
					int s=x+y+z;
					System.out.println(String.format("\ta=%d, b%d -> x=%d, y=%d, z=%d. Suma=%d.",p.a,p.b,x,y,z,s));
					sum+=s;
				}
				System.out.println(String.format("\tSuma total para %d: %d.",i,sum));
			}
		}
	}
}
