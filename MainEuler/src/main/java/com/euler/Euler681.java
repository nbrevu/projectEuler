package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler681 {
	private final static int N=1000000;
	
	private static class Factorer	{
		private final long[] lastPrimes;
		public Factorer(long n)	{
			lastPrimes=Primes.lastPrimeSieve(n);
		}
		public long[] getSquareSortedFactors(long i)	{
			DivisorHolder factors=DivisorHolder.getFromFirstPrimes(i,lastPrimes);
			long[] result=factors.pow(2).getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(result);
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Factorer factorer=new Factorer(N);
		long result=0;
		for (long i=1;i<=N;++i)	{
			long i2=i*i;
			long[] divisors=factorer.getSquareSortedFactors(i);
			int l=divisors.length;
			long maxA=LongMath.sqrt(i,RoundingMode.DOWN);
			for (int ia=0;ia<l;++ia)	{
				long a=divisors[ia];
				if (a>maxA) break;
				long maxB=(long)Math.floor(Math.cbrt(i2/a));
				for (int ib=ia;ib<l;++ib)	{
					long b=divisors[ib];
					if (b>maxB) break;
					long sumAB=a+b;
					long prodAB=a*b;
					if ((i2%prodAB)!=0) continue;
					long qAB=i2/prodAB;
					if ((((a+b)%2)==0)&&((qAB%4)==2)) continue;
					long maxC=LongMath.sqrt(qAB,RoundingMode.DOWN);
					for (int ic=ib;ic<l;++ic)	{
						long c=divisors[ic];
						if (c>maxC) break;
						long prodABC=prodAB*c;
						if ((i2%prodABC)!=0) continue;
						long d=i2/prodABC;
						long sumABC=sumAB+c;
						if (sumABC<=d) continue;	// Not a quadrilateral.
						long perimeter=sumABC+d;
						/*
						 * Surprisingly, checking that the "perimeter" is even is enough. Explanation:
						 * We have a*b*c*d = A^2. Now, we would need the sides to be, actually, (s-a), (s-b), (s-c) and (s-d) for some
						 * semiperimeter s. So, without renaming the code's variables, let's call e=s-a, f=s-b, g=s-c and h=s-d.
						 * These are the actual sides. Now, the perimeter would be 4*s-(a+b+c+d). However, since (a+b+c+d)=2s, it so happens
						 * that (e+f+g+h)=4s-(a+b+c+d)=4s-2s=2s=(a+b+c+d). MAGIC!
						 * 
						 * If a+b+c+d is not even, s is not an integer and the area is also not an integer. The perimeter would still be a+b+c+d.
						 * 
						 * As for the other condition, a+b+c<=d, we can reevaluate it in term of sides as: 3s-(e+f+g) <= s-h. Subtracting s and
						 * considering that P=2s is the perimeter, we get P-(e+f+g) <= -h. Of course the sum of all sides is the perimeter, so this
						 * translates as h <= -h, i.e. h<=0. This condition is actually enough, because if it doesn't hold, then it means that the
						 * smallest side (which is h by construction) is >0, therefore every side is. Now, by construction we have that every value
						 * in the set {e,f,g,h} is also below s. In particular, since the sum of all four is 2s, we now know for every side x<s we
						 * have that the sum of the other three lengths is 2s-x>s>x, so the quadrilateral is correct.
						 */
						if ((perimeter%2)==0) result+=perimeter;
					}
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
