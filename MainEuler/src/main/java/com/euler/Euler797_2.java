package com.euler;

import java.math.BigInteger;

public class Euler797_2 {
	public static void main(String[] args)	{
		for (int i=0;i<=4;++i)	{
			int power=1<<i;
			int rePower=1<<power;
			int valid=0;
			for (int j=0;j<rePower;++j)	{
				int accumulator=0;
				for (int k=0;k<power;++k)	{
					int bit=1<<k;
					if ((bit&j)!=0) accumulator|=k;
				}
				if (accumulator==power-1) ++valid;
			}
			System.out.println("For "+i+" prime factors there are "+valid+" valid combinations :O.");
		}
		// https://oeis.org/A000371
		BigInteger[] numbers=new BigInteger[] {
				BigInteger.TWO,
				BigInteger.TWO,
				BigInteger.TEN,
				BigInteger.valueOf(218),
				BigInteger.valueOf(64594),
				BigInteger.valueOf(4294642034l),
				new BigInteger("18446744047940725978"),
				new BigInteger("340282366920938463334247399005993378250"),
				new BigInteger("115792089237316195423570985008687907850547725730273056332267095982282337798562")
		};
		for (int i=0;i<numbers.length;++i)	{
			BigInteger total=BigInteger.TWO.pow(1<<i);
			BigInteger remaining=total.subtract(numbers[i]);
			System.out.println(String.format("If I have %d prime factors, there are %s total cases; %s are valid and %s are invalid.",i,total,numbers[i],remaining));
		}
	}
}
