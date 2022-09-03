package com.euler;

import java.math.RoundingMode;

import com.euler.common.SumOfTotientCalculator;
import com.google.common.math.DoubleMath;
import com.google.common.math.LongMath;

public class Euler643_2 {
	private final static long LIMIT=LongMath.pow(10l,11);
	private final static long MOD=LongMath.pow(10l,9)+7;

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SumOfTotientCalculator calculator=SumOfTotientCalculator.getWithMod(MOD);
		long result=0;
		long n=LIMIT/2;
		while (n>0)	{
			result+=calculator.getTotientSum(n);
			n/=2;
		}
		result-=DoubleMath.roundToLong(Math.log(LIMIT)/Math.log(2),RoundingMode.DOWN);
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
