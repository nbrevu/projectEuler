package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.euler.common.GenericLucyHedgehogSieve;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler642_3 {
	private final static long LIMIT=201820182018l;
	
	private static class BigPrimeSummation extends GenericLucyHedgehogSieve<BigInteger>	{
		private BigPrimeSummation(long upTo) {
			super(upTo);
		}
		@Override
		protected BigInteger f(long in) {
			return BigInteger.valueOf(in);
		}
		@Override
		protected BigInteger s(long in) {
			BigInteger bigIn=BigInteger.valueOf(in);
			return bigIn.multiply(bigIn.add(BigInteger.ONE)).shiftRight(1).subtract(BigInteger.ONE);
		}
		@Override
		protected BigInteger subtract(BigInteger min,BigInteger sub) {
			return min.subtract(sub);
		}
		@Override
		protected BigInteger multiply(BigInteger a,BigInteger b) {
			return a.multiply(b);
		}

		@Override
		protected boolean containsPrime(BigInteger sXMinusOne, BigInteger sX) {
			return !sXMinusOne.equals(sX);
		}
	}
	
	/*
	1344795111947631499044
	631499044
	Elapsed 7280.5461715 seconds.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger result=BigInteger.ZERO;
		long sqrtL=LongMath.sqrt(LIMIT,RoundingMode.UP);
		{
			// First stage.
			long[] primes=Primes.listLongPrimesAsArray(sqrtL);
			LongSet fullSet=HashLongSets.newMutableSet();
			fullSet.add(1l);
			for (long p:primes)	{
				long smallLimit=LIMIT/p;
				LongSet otherSet=HashLongSets.newMutableSet();
				for (LongCursor cursor=fullSet.cursor();cursor.moveNext();)	{
					long n=cursor.elem();
					if (n>smallLimit) cursor.remove();
					else do	{
						n*=p;
						otherSet.add(n);
					}	while (n<=smallLimit);
				}
				result=result.add(BigInteger.valueOf(otherSet.size()*p));
				fullSet.addAll(otherSet);
			}
		}
		BigPrimeSummation adder=new BigPrimeSummation(LIMIT);
		long lastLimit=sqrtL;
		BigInteger lastSum=adder.sumF(lastLimit);
		for (long q=sqrtL;q>=1;--q)	{
			long nextLimit=LIMIT/q;
			BigInteger nextSum=adder.sumF(nextLimit);
			result=result.add(nextSum.subtract(lastSum).multiply(BigInteger.valueOf(q)));
			lastLimit=nextLimit;
			lastSum=nextSum;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println(result.mod(BigInteger.valueOf(1_000_000_000l)));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
