package com.euler.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class BigRational	{
	public final static BigRational ZERO=new BigRational(BigInteger.ZERO);
	public final static BigRational ONE=new BigRational(BigInteger.ONE);
	public final BigInteger numerator;
	public final BigInteger denominator;
	private static BigInteger getGcd(BigInteger a,BigInteger b)	{
		for (;;)	{
			BigInteger r=a.mod(b);
			if (r.equals(BigInteger.ZERO)) return b;
			a=b;
			b=r;
		}
	}
	public BigRational()	{
		this(BigInteger.ZERO);
	}
	public BigRational(BigInteger numerator)	{
		this.numerator=numerator;
		denominator=BigInteger.ONE;
	}
	public BigRational(long n,long d)	{
		this(BigInteger.valueOf(n),BigInteger.valueOf(d));
	}
	public BigRational(BigInteger n,BigInteger d)	{
		BigInteger g=getGcd(n,d);
		numerator=n.divide(g);
		denominator=d.divide(g);
	}
	public BigRational multiply(long other)	{
		return multiply(BigInteger.valueOf(other));
	}
	public BigRational multiply(BigRational other)	{
		BigInteger newNumerator=numerator.multiply(other.numerator);
		BigInteger newDenominator=denominator.multiply(other.denominator);
		return new BigRational(newNumerator,newDenominator);
	}
	public BigRational multiply(BigInteger other)	{
		BigInteger newNumerator=numerator.multiply(other);
		return new BigRational(newNumerator,denominator);
	}
	public BigRational add(BigRational other)	{
		BigInteger newNumerator=numerator.multiply(other.denominator).add(other.numerator.multiply(denominator));
		BigInteger newDenominator=denominator.multiply(other.denominator);
		return new BigRational(newNumerator,newDenominator);
	}
	public BigRational divide(BigInteger other)	{
		BigInteger newDenominator=denominator.multiply(other);
		return new BigRational(numerator,newDenominator);
	}
	public BigRational divide(BigRational other)	{
		BigInteger newNumerator=numerator.multiply(other.denominator);
		BigInteger newDenominator=denominator.multiply(other.numerator);
		return new BigRational(newNumerator,newDenominator);
	}
	public BigRational subtract(BigRational other)	{
		BigInteger newNumerator=numerator.multiply(other.denominator).subtract(other.numerator.multiply(denominator));
		BigInteger newDenominator=denominator.multiply(other.denominator);
		return new BigRational(newNumerator,newDenominator);
	}
	public BigRational negate()	{
		return new BigRational(numerator.negate(),denominator);
	}
	public BigDecimal asBigDecimal()	{
		return (new BigDecimal(numerator)).divide(new BigDecimal(denominator));
	}
	public BigDecimal asBigDecimal(MathContext mc)	{
		return (new BigDecimal(numerator)).divide(new BigDecimal(denominator),mc);
	}
	@Override
	public String toString()	{
		StringBuilder sb=new StringBuilder();
		sb.append(numerator.toString()).append('/').append(denominator.toString());
		return sb.toString();
	}
	public double toDouble()	{
		return numerator.doubleValue()/denominator.doubleValue();
	}
}