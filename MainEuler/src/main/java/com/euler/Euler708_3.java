package com.euler;

import com.euler.common.MeisselLehmerPrimeCounter;
import com.google.common.math.LongMath;

public class Euler708_3 {
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		MeisselLehmerPrimeCounter counter=new MeisselLehmerPrimeCounter(10_000_000l);
		long pow=LongMath.pow(10l,13);
		System.out.println("π("+pow+")="+counter.pi(pow)+".");
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
