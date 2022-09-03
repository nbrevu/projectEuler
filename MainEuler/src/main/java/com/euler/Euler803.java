package com.euler;

import java.math.BigInteger;

import com.google.common.math.LongMath;

public class Euler803 {
	private final static BigInteger F=BigInteger.valueOf(25214903917l);
	private final static BigInteger K=BigInteger.valueOf(11);
	private final static BigInteger MOD=BigInteger.valueOf(LongMath.pow(2l,48));
	
	// private final static BigInteger A0=BigInteger.valueOf(123456);
	private final static BigInteger A0=BigInteger.valueOf(797264506701015l);
	
	public static void main(String[] args)	{
		BigInteger a=A0;
		StringBuilder sb=new StringBuilder();
		for (int i=0;i<109;++i)	{
			long b=(a.longValueExact()>>16)%52;
			char c=(char)((b<=25)?(b+'a'):(b-26+'A'));
			sb.append(c);
			System.out.println(a+" => "+c+"...");
			a=a.multiply(F).add(K).mod(MOD);
		}
		System.out.println(sb.toString());
	}
}
