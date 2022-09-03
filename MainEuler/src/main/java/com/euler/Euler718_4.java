package com.euler;

import java.math.BigInteger;
import java.util.Arrays;

import com.google.common.math.LongMath;

public class Euler718_4 {
	private final static int P=6;
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private final static long P1=LongMath.pow(17,P);
	private final static long P2=LongMath.pow(19,P);
	private final static long P3=LongMath.pow(23,P);
	// Frobenius numbers calculated using Mathematica.
	private final static long[] FROBENIUS_ARRAY=new long[] {176l, 18422l, 1376626l, 110608186l, 9774616654l, 869836853153l};
	private final static long FROBENIUS_NUMBER=FROBENIUS_ARRAY[P-1];
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] smallestModuloP1=new long[(int)P1];
		Arrays.fill(smallestModuloP1,Long.MAX_VALUE);
		for (long r=P3;r<=FROBENIUS_NUMBER;r+=P3) for (long q=r+P2;q<=FROBENIUS_NUMBER;q+=P2)	{
			int bucket=(int)(q%P1);
			smallestModuloP1[bucket]=Math.min(smallestModuloP1[bucket],q);
		}
		BigInteger bigResult=BigInteger.ZERO;
		for (int i=0;i<smallestModuloP1.length;++i)	{
			BigInteger smallest=BigInteger.valueOf(i);
			BigInteger biggest=BigInteger.valueOf(smallestModuloP1[i]);
			BigInteger howMany=BigInteger.valueOf(1+((smallestModuloP1[i]-i)/P1));
			bigResult=bigResult.add(smallest.add(biggest).multiply(howMany).divide(BigInteger.TWO));
		}
		long result=bigResult.mod(BigInteger.valueOf(MOD)).longValueExact();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(bigResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
