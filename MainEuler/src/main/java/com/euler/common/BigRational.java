package com.euler.common;

import static com.euler.common.EulerUtils.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class BigRational implements Comparable<BigRational>	{
	public final static BigRational ZERO=new BigRational(BigInteger.ZERO,BigInteger.ONE);
	public final static BigRational ONE=new BigRational(BigInteger.ONE,BigInteger.ONE);
	
	public final BigInteger num;
	public final BigInteger den;
	public BigRational(long integer)	{
		this.num=BigInteger.valueOf(integer);
		this.den=BigInteger.ONE;
	}
	public BigRational(BigInteger integer)	{
		this.num=integer;
		this.den=BigInteger.ONE;
	}
	public BigRational(long num,long den)	{
		this(BigInteger.valueOf(num),BigInteger.valueOf(den));
	}
	public BigRational(BigInteger num,BigInteger den)	{
		if (den.signum()==0) throw new IllegalArgumentException("Das geht nicht. No me gusta dividir por números insuficientemente no nulos.");
		BigInteger gcd=gcd(num,den);
		num=num.divide(gcd);
		den=den.divide(gcd);
		if (den.signum()<0)	{
			num=num.negate();
			den=den.negate();
		}
		this.num=num;
		this.den=den;
	}
	public BigRational sum(BigRational other)	{
		BigInteger lcm=lcm(den,other.den);
		BigInteger numA=num.multiply(lcm.divide(den));
		BigInteger numB=other.num.multiply(lcm.divide(other.den));
		return new BigRational(numA.add(numB),lcm);
	}
	public BigRational subtract(BigRational other)	{
		BigInteger lcm=lcm(den,other.den);
		BigInteger numA=num.multiply(lcm.divide(den));
		BigInteger numB=other.num.multiply(lcm.divide(other.den));
		return new BigRational(numA.subtract(numB),lcm);
	}
	public BigRational multiply(BigRational other)	{
		return new BigRational(num.multiply(other.num),den.multiply(other.den));
	}
	public BigRational divide(BigRational other)	{
		return new BigRational(num.multiply(other.den),den.multiply(other.num));
	}
	public BigRational negate()	{
		return new BigRational(num.negate(),den);
	}
	public boolean isNotZero()	{
		return num.signum()!=0;
	}
	@Override
	public boolean equals(Object other)	{
		if (!(other instanceof BigRational)) return false;
		BigRational rOther=(BigRational)other;
		return num.equals(rOther.num)&&den.equals(rOther.den);
	}
	@Override
	public int hashCode()	{
		return 31*num.hashCode()+den.hashCode();
	}
	@Override
	public String toString()	{
		return (den.equals(BigInteger.ONE))?num.toString():String.format("%s/%s",num.toString(),den.toString());
	}
	@Override
	public int compareTo(BigRational o) {
		/*
		 * Assumes that the denominators are positive, as per construction.
		 * The condition is n/d < o.n/o.d.
		 * Since d and o.d are positive, the operator doesn't change after multiplying times d*o.d:
		 * n*o.d < o.n*d
		 */
		return num.multiply(o.den).compareTo(o.num.multiply(den));
	}
	public double toDouble()	{
		return num.doubleValue()/den.doubleValue();
	}
	public double toDouble(int nDigits)	{
		return new BigDecimal(num).divide(new BigDecimal(den),nDigits,RoundingMode.HALF_UP).doubleValue();
	}
}