package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler473 {
	private final static long LIMIT=LongMath.pow(10l,10);
	
	private final static double PHI=(1.0+Math.sqrt(5.0))/2.0;
	private final static int MAX_PHIGITS=(int)Math.floor(Math.log((double)LIMIT)/Math.log(PHI));
	
	private static class PhiPower	{
		public final long intPart;
		public final long sqrt5Part;
		public final long denominator;
		private PhiPower(long intPart,long sqrt5Part,long denominator)	{
			this.intPart=intPart;
			this.sqrt5Part=sqrt5Part;
			this.denominator=denominator;
		}
		public final static PhiPower PHI=new PhiPower(1l,1l,2l);
		public final static PhiPower PHI_MINUS_ONE=new PhiPower(-1l,1l,2l);
		public PhiPower multiply(PhiPower other)	{
			long newIntPart=intPart*other.intPart+5*sqrt5Part*other.sqrt5Part;
			long newSqrt5Part=intPart*other.sqrt5Part+other.intPart*sqrt5Part;
			long newDenominator=denominator*other.denominator;
			long gcd=Math.abs(EulerUtils.gcd(EulerUtils.gcd(newIntPart,newSqrt5Part),newDenominator));
			return new PhiPower(newIntPart/gcd,newSqrt5Part/gcd,newDenominator/gcd);
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append('(');
			sb.append(intPart);
			sb.append(" + ");
			sb.append(sqrt5Part);
			sb.append("*sqrt(5))/");
			sb.append(denominator);
			return sb.toString();
		}
	}
	
	private static class IntegerPlusPhiMultiple	{
		public final long intPart;
		public final long sqrt5Part;
		private IntegerPlusPhiMultiple(long intPart,long sqrt5Part)	{
			this.intPart=intPart;
			this.sqrt5Part=sqrt5Part;
		}
		public static IntegerPlusPhiMultiple addPhiPowers(PhiPower p1,PhiPower p2)	{
			if (p1.denominator!=p2.denominator) throw new IllegalArgumentException("Algo has hecho muy, muy mal.");
			long denom=p1.denominator;
			long intPart=p1.intPart+p2.intPart;
			if ((intPart%denom)!=0) throw new IllegalArgumentException("Estas potencias no son compatibles.");
			long sqrt5Part=p1.sqrt5Part+p2.sqrt5Part;
			if ((sqrt5Part%denom)!=0) throw new IllegalArgumentException("Estas potencias no son compatibles.");
			return new IntegerPlusPhiMultiple(intPart/denom,sqrt5Part/denom);
		}
		public IntegerPlusPhiMultiple add(IntegerPlusPhiMultiple other)	{
			return new IntegerPlusPhiMultiple(intPart+other.intPart,sqrt5Part+other.sqrt5Part);
		}
		public boolean isInteger()	{
			return sqrt5Part==0;
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			sb.append(intPart).append(" + ").append(sqrt5Part).append("*sqrt(5)");
			return sb.toString();
		}
	}
	
	public static void main(String[] args)	{
		PhiPower[] positivePowers=new PhiPower[MAX_PHIGITS];
		PhiPower[] negativePowers=new PhiPower[MAX_PHIGITS];
		positivePowers[0]=PhiPower.PHI;
		negativePowers[0]=PhiPower.PHI_MINUS_ONE;
		for (int i=1;i<MAX_PHIGITS;++i)	{
			positivePowers[i]=positivePowers[i-1].multiply(PhiPower.PHI);
			negativePowers[i]=negativePowers[i-1].multiply(PhiPower.PHI_MINUS_ONE);
		}
		IntegerPlusPhiMultiple[] symmetricElements=new IntegerPlusPhiMultiple[MAX_PHIGITS];
		for (int i=0;i<MAX_PHIGITS;++i) symmetricElements[i]=IntegerPlusPhiMultiple.addPhiPowers(positivePowers[i],negativePowers[i]);
		/*
		 *  At this point, there are only two types of elements:
		 *   - Elements with ODD index (1..45). They are positive integers.
		 *   - Elements with EVEN index (0..46). They are positive multiples of sqrt(5).
		 *  Obviously no even indices must be included; there is no way that a nonzero combination of
		 *  strictly positive numbers results in zero.
		 *  This leaves us with the positive numbers, which by the way follow this recursive formula:
		 *   a_0=3
		 *   a_1=7
		 *   a_n=3*a_(n-1) - a_(n-2).
		 *  We just need to generate the 2^23 combinations.
		 */
		int finalElements=MAX_PHIGITS/2;
		long[] augends=new long[finalElements];
		for (int i=0;i<finalElements;++i) augends[i]=symmetricElements[2*i+1].intPart;
		// ACHTUNG, esto está mal. Hay que hilar más fino :(.
	}
}
