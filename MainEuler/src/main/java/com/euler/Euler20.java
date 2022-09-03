package com.euler;

import java.math.BigInteger;

public class Euler20 {
	public static void main(String[] args)	{
		BigInteger fact=BigInteger.valueOf(1);
		for (int i=2;i<=100;++i) fact=fact.multiply(BigInteger.valueOf(i));
		String toAdd=fact.toString();
		int res=0;
		for (int i=0;i<toAdd.length();++i) res+=toAdd.charAt(i)-'0';
		System.out.println(res);
	}
}
