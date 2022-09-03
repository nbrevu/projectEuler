package com.euler.experiments;

import java.math.BigInteger;

public class RepeatedCubes {
	public static void main(String[] args)	{
		BigInteger ten14=BigInteger.TEN.pow(14);
		BigInteger experiment1=ten14.multiply(ten14).add(ten14).add(BigInteger.ONE);
		BigInteger remainder1=experiment1.mod(BigInteger.valueOf(49));
		BigInteger ten98=ten14.pow(7);
		BigInteger experiment2=ten98.multiply(ten98).add(ten98).add(BigInteger.ONE);
		BigInteger remainder2=experiment2.mod(BigInteger.valueOf(343));
		System.out.println("Primer experimento: el resto es "+remainder1+".");
		System.out.println("Segundo experimento: el resto es "+remainder2+".");
		BigInteger candidate1=experiment1.divide(BigInteger.valueOf(7));
		System.out.println(candidate1+" al cubo es "+candidate1.pow(3)+".");
		BigInteger candidate2=experiment2.divide(BigInteger.valueOf(49));
		/*
		 * OF COURSE. This doesn't work because 1...0...1...0...1/49 should be smaller than THE SQUARE ROOT of 1...0...1...0...1 in order to work.
		 * I'll need to stick to squares. Too bad.
		 */
		System.out.println(candidate2+" al cubo es "+candidate2.pow(3)+".");
	}
}
