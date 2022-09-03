package com.euler;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.math.LongMath;

public class Euler359 {
	// The number asked for, 71328803586048, is 2^27*3^12.
	private final static int POWER2=27;
	private final static int POWER3=12;
	
	private final static long MOD=LongMath.pow(10,8);
	
	private final static BigInteger TWO=BigInteger.valueOf(2l);

	private static class HilbertArguments	{
		public final long f;
		public final long r;
		public HilbertArguments(long f,long r)	{
			this.f=f;
			this.r=r;
		}
	}
	
	private static long[] getPowers(long in,int upTo)	{
		long[] result=new long[1+upTo];
		result[0]=1;
		for (int i=1;i<=upTo;++i) result[i]=in*result[i-1];
		return result;
	}
	
	private static Set<HilbertArguments> generatePairs(int powerOf2,int powerOf3)	{
		long[] powersOf2=getPowers(2l,powerOf2);
		long[] powersOf3=getPowers(3l,powerOf3);
		NavigableSet<Long> allFactors=new TreeSet<>();
		for (int i=0;i<=powerOf2;++i) for (int j=0;j<=powerOf3;++j) allFactors.add(powersOf2[i]*powersOf3[j]);
		long actualNumber=allFactors.last();
		Set<HilbertArguments> result=new HashSet<>();
		for (long f:allFactors)	{
			long r=actualNumber/f;
			result.add(new HilbertArguments(f,r));
		}
		return result;
	}
	
	private static interface SequenceGenerator	{
		public BigInteger getNumber(long position);
	}
	
	public static class TriangularNumbers implements SequenceGenerator	{
		@Override
		public BigInteger getNumber(long position)	{
			BigInteger n1=BigInteger.valueOf(position);
			BigInteger n2=n1.add(BigInteger.ONE);
			return n1.multiply(n2).divide(TWO);
		}
	}
	
	public static class HilbertSequence implements SequenceGenerator	{
		private final BigInteger initialNumber;
		private final BigInteger initialOddDelta;	// Seq(2) - Seq(1)
		private final BigInteger initialEvenDelta;	// Seq(3) - Seq(2)
		
		public HilbertSequence(long rowNumber)	{
			BigInteger p1=BigInteger.valueOf(rowNumber/2);
			BigInteger p2=BigInteger.valueOf((rowNumber+1)/2);
			initialNumber=p1.multiply(p2).multiply(TWO);
			if ((rowNumber%2)==0)	{
				initialOddDelta=BigInteger.valueOf(2*rowNumber+1);
				initialEvenDelta=TWO;
			}	else	{
				initialOddDelta=BigInteger.ONE;
				initialEvenDelta=BigInteger.valueOf(2*rowNumber);
			}
		}

		@Override
		public BigInteger getNumber(long position) {
			/*
			Primer incremento: X
			Segundo incremento: X+4
			Tercer incremento: X+8
			...
			Enésimo incremento: X+(N-1)*4
			
			Suma de los primeros N incrementos: N*X + 4*triang(N-1) = N*X + 2*N*(N-1) = N*(X+2*N-2)
			*/
			if (position==1) return initialNumber;
			if (position==2) return initialNumber.add(initialOddDelta);
			BigInteger base,firstIncrement,howManyIncrements;
			if ((position%2)==0)	{
				base=initialNumber.add(initialOddDelta);
				firstIncrement=initialOddDelta.add(initialEvenDelta).add(TWO);
				howManyIncrements=BigInteger.valueOf((position/2)-1);
			}	else	{
				base=initialNumber;
				firstIncrement=initialOddDelta.add(initialEvenDelta);
				howManyIncrements=BigInteger.valueOf((position-1)/2);
			}
			BigInteger totalIncrement=howManyIncrements.multiply(firstIncrement.add(howManyIncrements.multiply(TWO)).subtract(TWO));
			return base.add(totalIncrement);
		}
	}
	
	private static BigInteger getHilbertNumber(HilbertArguments ha)	{
		SequenceGenerator sequence=(ha.f==1)?new TriangularNumbers():new HilbertSequence(ha.f);
		return sequence.getNumber(ha.r);
	}
	
	public static void main(String[] args)	{
		BigInteger result=BigInteger.ZERO;
		for (HilbertArguments ha:generatePairs(POWER2,POWER3)) result=result.add(getHilbertNumber(ha));
		result=result.mod(BigInteger.valueOf(MOD));
		System.out.println(result);
	}
}
