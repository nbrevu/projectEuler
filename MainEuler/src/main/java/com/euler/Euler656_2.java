package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;

import com.euler.common.Convergents.Convergent;
import com.euler.common.Convergents.PeriodicContinuedFraction;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler656_2 {
	private final static int LIMIT=1000;
	private final static int COUNT=100;
	private final static BigInteger MOD=BigInteger.valueOf(LongMath.pow(10l,15));
	
	private static class PalindromeIndexIterator implements Iterator<BigInteger>	{
		private static class Group implements Iterator<BigInteger>	{
			private BigInteger currentValue;
			private final BigInteger increment;
			private final BigInteger lastValue;
			public Group(BigInteger initialValue,BigInteger increment,BigInteger lastValue)	{
				this.currentValue=initialValue;
				this.increment=increment;
				this.lastValue=lastValue;
			}
			@Override
			public boolean hasNext() {
				return currentValue.compareTo(lastValue)<0;
			}
			@Override
			public BigInteger next() {
				BigInteger result=currentValue;
				currentValue=currentValue.add(increment);
				return result;
			}
		}
		private final Iterator<Convergent> convergents;
		private Group currentGroup;
		private BigInteger lastQ;
		public PalindromeIndexIterator(int n)	{
			convergents=PeriodicContinuedFraction.getForSquareRootOf(n).iterator();
			convergents.next();
			BigInteger firstQ=convergents.next().q;
			currentGroup=new Group(BigInteger.ONE,BigInteger.ONE,firstQ);
			lastQ=firstQ;
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public BigInteger next() {
			if (!currentGroup.hasNext())	{
				BigInteger increment=convergents.next().q;
				BigInteger newQ=convergents.next().q;
				currentGroup=new Group(lastQ,increment,newQ);
				lastQ=newQ;
			}
			return currentGroup.next();
		}
	}
	
	private static boolean isPerfectSquare(int in)	{
		int sq=IntMath.sqrt(in,RoundingMode.DOWN);
		return (sq*sq)==in;
	}
	
	private static BigInteger getH(int count,int beta)	{
		Iterator<BigInteger> palindromeIndexIterator=new PalindromeIndexIterator(beta);
		BigInteger result=BigInteger.ZERO;
		for (int i=0;i<count;++i) result=result.add(palindromeIndexIterator.next());
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger result=BigInteger.ZERO;
		for (int i=2;i<=LIMIT;++i) if (!isPerfectSquare(i)) result=result.add(getH(COUNT,i));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result.mod(MOD));
	}
}
