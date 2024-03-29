package com.euler.common;

import static com.euler.common.EulerUtils.*;

import java.util.NoSuchElementException;

public class Rational implements Comparable<Rational>	{
	public final static Rational ZERO=new Rational(0,1);
	public final static Rational ONE=new Rational(1,1);
	private final long num;
	private final long den;
	public Rational(long integer)	{
		this.num=integer;
		this.den=1;
	}
	public Rational(long num,long den)	{
		if (den==0) throw new IllegalArgumentException("Das geht nicht. No me gusta dividir por números insuficientemente no nulos.");
		long gcd=gcd(num,den);
		num/=gcd;
		den/=gcd;
		if (den<0)	{
			num=-num;
			den=-den;
		}
		this.num=num;
		this.den=den;
	}
	public boolean isInteger()	{
		return (num%den)==0;
	}
	public long getIntegerValue()	{
		if (!isInteger()) throw new NoSuchElementException("QUE TE HE DICHO QUE NO ES UN ENTERO, HOSTIAS.");
		return num/den;
	}
	public Rational sum(Rational other)	{
		long lcm=lcm(den,other.den);
		long numA=num*(lcm/den);
		long numB=other.num*(lcm/other.den);
		return new Rational(numA+numB,lcm);
	}
	public Rational subtract(Rational other)	{
		long lcm=lcm(den,other.den);
		long numA=num*(lcm/den);
		long numB=other.num*(lcm/other.den);
		return new Rational(numA-numB,lcm);
	}
	public Rational multiply(long integer)	{
		return new Rational(num*integer,den);
	}
	public Rational multiply(Rational other)	{
		return new Rational(num*other.num,den*other.den);
	}
	public Rational divide(Rational other)	{
		return new Rational(num*other.den,den*other.num);
	}
	public Rational negate()	{
		return new Rational(-num,den);
	}
	public boolean isNotZero()	{
		return num!=0;
	}
	@Override
	public boolean equals(Object other)	{
		if (!(other instanceof Rational)) return false;
		Rational rother=(Rational)other;
		return (num==rother.num)&&(den==rother.den);
	}
	@Override
	public int hashCode()	{
		return 31*(int)num+(int)den;
	}
	@Override
	public String toString()	{
		if (isInteger()) return Long.toString(getIntegerValue());
		return num+"/"+den;
	}
	public double toDouble()	{
		return (double)num/(double)den;
	}
	public long num()	{
		return num;
	}
	public long den()	{
		return den;
	}
	@Override
	public int compareTo(Rational other)	{
		boolean isDiffDenomNegative=(den<0)^(other.den<0);
		// Overflows might happen!
		int result=Long.compare(num*other.den,den*other.num);
		if (isDiffDenomNegative) result=-result;
		return result;
	}
	public static Rational min(Rational a,Rational b)	{
		return (a.compareTo(b)<=0)?a:b;
	}
	public static Rational max(Rational a,Rational b)	{
		return (a.compareTo(b)<=0)?b:a;
	}
}