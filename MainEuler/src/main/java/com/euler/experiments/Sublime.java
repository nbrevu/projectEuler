package com.euler.experiments;

import java.math.BigInteger;

public class Sublime {
	public static void main(String[] args)	{
		System.out.println("Número de divisores: "+(127*64)+".");
		BigInteger divisorSum=BigInteger.TWO.pow(127).subtract(BigInteger.ONE).multiply(BigInteger.TWO.pow(126));
		System.out.println(divisorSum);
	}
}
