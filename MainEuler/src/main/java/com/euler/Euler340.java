package com.euler;

import static java.math.BigInteger.ONE;

import java.math.BigInteger;

import com.google.common.math.LongMath;

public class Euler340 {
	//*
	private final static long A=LongMath.pow(21l,7);
	private final static long B=LongMath.pow(7l,21);
	private final static long C=LongMath.pow(12l,7);
	/*/
	private final static long A=50;
	private final static long B=2000;
	private final static long C=40;
	//*/
	private final static BigInteger TWO=BigInteger.valueOf(2l);
	private final static BigInteger THREE=BigInteger.valueOf(3l);
	private final static BigInteger FOUR=BigInteger.valueOf(4l);

	private final static long MOD=1000000000l;
	
	public static void main(String[] args)	{
		BigInteger a=BigInteger.valueOf(A);
		BigInteger b=BigInteger.valueOf(B);
		BigInteger c=BigInteger.valueOf(C);
		BigInteger m=BigInteger.valueOf(B%A);
		BigInteger q=BigInteger.valueOf(B/A);
		BigInteger f1=a.subtract(c);
		BigInteger s1=m.add(ONE).multiply(f1.multiply(THREE.multiply(q).add(FOUR)).add(b));
		BigInteger s2=a.multiply(f1).multiply(FOUR.multiply(q).add(THREE.multiply(q).multiply(q.subtract(ONE)).divide(TWO))).add(a.multiply(b).multiply(q));
		BigInteger s3=a.multiply(a.subtract(ONE)).multiply(q).add(m.multiply(m.add(ONE))).divide(TWO);
		BigInteger result=s1.add(s2).subtract(s3).mod(BigInteger.valueOf(MOD));
		System.out.println(result);
	}
}
