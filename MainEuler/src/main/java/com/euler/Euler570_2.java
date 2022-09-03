package com.euler;

import java.math.BigInteger;

public class Euler570_2 {
	private final static int ORDER=10000000;
	
	private final static BigInteger TWO=BigInteger.valueOf(2);
	private final static BigInteger THREE=BigInteger.valueOf(3);
	private final static BigInteger FOUR=BigInteger.valueOf(4);
	
	public static void main(String[] args)	{
		BigInteger A1=BigInteger.valueOf(12);
		BigInteger A2=BigInteger.valueOf(6);
		BigInteger B=BigInteger.valueOf(6);
		BigInteger F1=BigInteger.valueOf(-17);
		BigInteger F2=BigInteger.valueOf(17);
		BigInteger res=BigInteger.ZERO;
		for (int i=3;i<=ORDER;++i)	{
			if ((i%1000)==0) System.out.println(""+i+"...");
			A1=A1.multiply(FOUR);
			A2=A2.multiply(FOUR);
			B=B.multiply(THREE);
			F1=F1.add(THREE);
			F2=F2.add(TWO);
			BigInteger t1=A1.subtract(B);
			BigInteger t3=F1.multiply(A2).add(F2.multiply(B));
			res=res.add(t1.gcd(t3));
		}
		System.out.println(res.toString());
	}
}
