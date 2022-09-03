package com.euler;

public class Euler100 {
	/*
	private final static BigInteger TWO=BigInteger.valueOf(2);
	private final static BigInteger THREE=BigInteger.valueOf(3);
	private final static BigInteger FOUR=BigInteger.valueOf(4);
	private final static BigInteger EURO_BILLION=BigInteger.valueOf((long)1e12);
	
	public static void main(String[] args)	{
		BigInteger a=BigInteger.valueOf(15);
		BigInteger b=BigInteger.valueOf(21);
		do	{
			BigInteger newA=a.multiply(THREE).add(b.multiply(TWO)).subtract(TWO);
			BigInteger newB=a.multiply(FOUR).add(b.multiply(THREE)).subtract(THREE);
			a=newA;
			b=newB;
		}	while (b.compareTo(EURO_BILLION)<0);
		System.out.println(a.toString());
	}
	*/
	
	public final static long LIMIT=1000000000000l;
	
	public static void main(String[] args)	{
		long a=15;
		long b=21;
		do	{
			long newA=3*a+2*b-2;
			long newB=4*a+3*b-3;
			a=newA;
			b=newB;
		}	while (b<LIMIT);
		System.out.println(a);
	}
}
