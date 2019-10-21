package com.euler.common;

public class Triangle	{
	public final long a;
	public final long b;
	public final long c;
	public Triangle(long a,long b,long c)	{
		this.a=a;
		this.b=b;
		this.c=c;
	}
	@Override
	public int hashCode()	{
		return Long.hashCode(a+b+c);
	}
	@Override
	public boolean equals(Object other)	{
		if (!(other instanceof Triangle)) return false;
		Triangle tO=(Triangle)other;
		return ((a==tO.a)&&(b==tO.b)&&(c==tO.c));
	}
	@Override
	public String toString()	{
		return "["+a+','+b+','+c+']';
	}
}