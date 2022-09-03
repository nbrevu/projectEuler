package com.euler.experiments;

import java.math.BigInteger;

public class RepeatedSquare {
	// ES GEFÄLLT MIR :D.
	/*
	 * Special, über-devilish version: try to find the smallest base which is not a multiple of ten and for which the smallest "repeated" square
	 * is bigger than the one for base 10. Investigate! 
	 */
	public static void main(String[] args)	{
		{
			BigInteger tenPowPlusOne=BigInteger.TEN.pow(21).add(BigInteger.ONE);
			BigInteger square=tenPowPlusOne.multiply(tenPowPlusOne).divide(BigInteger.valueOf(49l));
			BigInteger result=square.multiply(BigInteger.valueOf(9l));
			BigInteger sqrt=result.sqrt();
			System.out.println(sqrt+" squared is "+(sqrt.multiply(sqrt))+"! Repeated number! Weeee!!!!!");
		}
		// Ok, this is a BIIIIIIIIIT of a disappointment. But it's cool!
		// ZUTUN! Investigate more! Does this happen for all the prime N+1 when N is a base? There's a lot of FUN to be had with this!!!!!
		{
			BigInteger tenPowPlusOne=BigInteger.TEN.pow(11).add(BigInteger.ONE);
			BigInteger square=tenPowPlusOne.multiply(tenPowPlusOne).divide(BigInteger.valueOf(121l));
			BigInteger result=square.multiply(BigInteger.valueOf(16l));
			BigInteger sqrt=result.sqrt();
			System.out.println(sqrt+" squared is "+(sqrt.multiply(sqrt))+"! Repeated number! Weeee!!!!!");
		}
	}
}
