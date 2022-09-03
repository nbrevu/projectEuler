package com.euler;

import java.math.BigInteger;

import com.euler.common.BigIntegerUtils.Fraction;

public class Euler389_3 {
	private static class Distribution	{
		public final Fraction mean;
		public final Fraction variance;
		public Distribution(Fraction mean,Fraction variance)	{
			this.mean=mean;
			this.variance=variance;
		}
		public Distribution multiply(int factor)	{
			BigInteger biFactor=BigInteger.valueOf(factor);
			return new Distribution(mean.multiply(biFactor),variance.multiply(biFactor));
		}
		@Override
		public String toString()	{
			return "["+mean+", "+variance+"]";
		}
	}
	
	private static Distribution getUniformDistribution(int n)	{
		BigInteger mNumerator=BigInteger.valueOf(n+1);
		BigInteger mDenominator=BigInteger.valueOf(2);
		Fraction mean=new Fraction(mNumerator,mDenominator);
		BigInteger vNumerator=BigInteger.valueOf((n*n)-1);
		BigInteger vDenominator=BigInteger.valueOf(12);
		Fraction variance=new Fraction(vNumerator,vDenominator);
		return new Distribution(mean,variance);
	}
	
	private static Distribution getComposedDistribution(Distribution base,int n)	{
		Distribution[] multiplied=new Distribution[n];
		multiplied[0]=base;
		for (int i=1;i<n;++i) multiplied[i]=base.multiply(i+1);
		Fraction prob=new Fraction(BigInteger.ONE,BigInteger.valueOf(n));
		Fraction mean=new Fraction();
		for (Distribution d:multiplied) mean=mean.add(d.mean);
		mean=mean.multiply(prob);
		Fraction variance=new Fraction();
		for (Distribution d:multiplied) variance=variance.add(d.variance.add(d.mean.multiply(d.mean)));
		variance=variance.multiply(prob);
		variance=variance.subtract(mean.multiply(mean));
		return new Distribution(mean,variance);
	}
	
	public static void main(String[] args)	{
		Distribution i=getUniformDistribution(20);
		Distribution d=getComposedDistribution(i,12);
		Distribution o=getComposedDistribution(d,8);
		Distribution h=getComposedDistribution(o,6);
		Distribution t=getComposedDistribution(h,4);
		System.out.println(t.variance.asBigDecimal());
	}
}