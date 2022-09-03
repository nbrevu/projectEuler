package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler757_3 {
	private final static long LIMIT=LongMath.pow(10l,14);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int expectedSize=10*(int)LongMath.sqrt(LIMIT,RoundingMode.UP);
		LongSet found=HashLongSets.newMutableSet(expectedSize);
		/*
		 * Instead of using a, b, c, d, we consider these values:
		 * alpha = (a+b)/2.
		 * beta = (a-b)/2.
		 * gamma = (c+d)/2.
		 * delta = (c-d)/2.
		 * I have noticed, but not proven, that it's always the case that alpha = gamma+0.5.
		 * We will iterate over beta and delta.
		 * One of (beta, delta) will be an integer, and the other one will be a half integer.
		 * Given beta and delta, there is exactly one value of alpha that satisfies N=ab=cd with a+b=c+d+1.
		 * This value is: alpha=beta^2-delta^2+0.25. It's easy to prove.
		 * It can be easily proven that alpha is a half integer if and only if beta is a half integer as well.
		 * Given some fixed beta, the greatest value of delta that will result in a non-zero N is beta-1.5. Then, we can move by decrements of 1.
		 * Smaller deltas result in bigger alphas, therefore in bigger Ns.
		 * 
		 * We iterate separately per half integers and integers.
		 */
		for (long b=2;;++b)	{
			// First part: beta is an integer.
			boolean foundSome=false;
			long bb=b*b;
			for (long d=b-2;d>=0;--d)	{
				/*
				 * Delta is a half integer. When we say "d=0", we actually mean delta=0.5.
				 * alpha=beta^2-delta^2+0.25=b^2-(d+0.5)^2+0.25=b^2-d^2-d.
				 */
				long a=bb-d*(d+1);
				long n=a*a-bb;
				if (n>LIMIT) break;
				foundSome=true;
				found.add(n);
			}
			if (!foundSome) break;
		}
		for (long b=1;;++b)	{
			// Second part: beta is a half integer. When we say "b=1", we actually mean beta=1.5.
			boolean foundSome=false;
			long bb1=b*(b+1);
			for (long d=b-1;d>=0;--d)	{
				/*
				 * alpha=(beta^2-delta^2+0.25)=b^2+b+d^2+0.5.
				 * "a" is the truncated alpha, i.e., alpha-0.5.
				 * We need to calculate alpha^2-beta^2=a*(a+1)-b*(b+1).
				 */
				long a=bb1-d*d;
				long n=a*(a+1)-bb1;
				if (n>LIMIT) break;
				foundSome=true;
				found.add(n);
			}
			if (!foundSome) break;
		}
		int result=found.size();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
