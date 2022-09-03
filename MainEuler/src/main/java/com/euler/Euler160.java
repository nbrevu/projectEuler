package com.euler;

import java.math.BigInteger;

public class Euler160 {
	/*
	// This doesn't go in "BigIntegerUtils" because it's specific.
	public static BigInteger factorialMod(BigInteger n,BigInteger mod)	{
		BigInteger prod=BigInteger.ONE;
		for (BigInteger i=BigInteger.ONE;i.compareTo(n)<0;i=i.add(BigInteger.ONE))	{
			prod=prod.multiply(i);
			while (prod.mod(BigInteger.TEN).equals(BigInteger.ZERO)) prod=prod.divide(BigInteger.TEN);
			prod=prod.mod(mod);
		}
		return prod;
	}
	
	public static void main(String[] args)	{
		BigInteger bigMod=BigIntegerUtils.pow(10, 40);
		// Note that 250 million is slightly higher than 5^12.
		BigInteger firstNumber=factorialMod(BigInteger.valueOf(250000000),bigMod);
		System.out.println(firstNumber.toString()+"...");
		BigInteger res=BigIntegerUtils.powMod(firstNumber, 4000, bigMod);
		System.out.println(res.toString());
	}
	*/
	
	public static void main(String[] args)	{
		BigInteger res=BigInteger.ONE;
		BigInteger limit=BigInteger.valueOf(1000000000000l);
		for (BigInteger i=BigInteger.ONE;i.compareTo(limit)<0;i=i.add(BigInteger.ONE))	{
			res=res.multiply(i);
			for (;;)	{
				BigInteger r[]=res.divideAndRemainder(BigInteger.TEN);
				if (r[1].equals(BigInteger.ZERO)) res=r[0];
				else break;
			}
		}
		BigInteger r=res.remainder(BigInteger.valueOf(100000));
		System.out.println(r.toString());
	}
}
