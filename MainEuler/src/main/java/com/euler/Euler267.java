package com.euler;

import static java.lang.Math.log;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;

import com.euler.common.BigIntegerUtils;
import com.euler.common.BigIntegerUtils.Fraction;

public class Euler267 {
	// Sea x el nº de caras.
	// F(x,h)=(1+2f)^x·(1-f)^(h-x)
	// 1e9 <= (1+2f)^x·(1-f)^(h-x)
	// 1e9 <= (1-f)^h· [(1+2f)/(1-f)]^x
	// 9 log 10 <= h·log(1-f) + x·log[(1+2f)/(1-f)].
	// x >= [9log10-h·log(1-f)]/log [(1+2f)/(1-f)]
	
	private final static double LOG_1E9=9*Math.log(10.0);
	private final static double DERIVATIVE_H=1e-7;
	private final static double INITIAL_F=0.1;
	private final static double TOLERANCE=1e-12;
	private final static double GAMMA=1e-4;
	
	private static double fun(double f)	{
		double f_1=1-f;
		double num=LOG_1E9-1000*log(f_1);
		double den=log((1+2*f)/f_1);
		return num/den;
	}
	
	private static double numDerivative(double f,double h)	{
		return (fun(f+h)-fun(f))/h;
	}
	
	private static BigInteger[] factorials=BigIntegerUtils.factorials(1000);

	private static BigInteger getCombinatorial(int n,int k)	{
		return factorials[n].divide(factorials[k].multiply(factorials[n-k]));
	}
	
	public static void main(String[] args)	{
		double f=INITIAL_F;
		for (;;)	{
			double dS=numDerivative(f,DERIVATIVE_H);
			if (Math.abs(dS)<TOLERANCE) break;
			f-=GAMMA*dS;
		}
		// So, the minimum amount of heads is...
		int x=(int)(Math.ceil(fun(f)));
		BigInteger result=ZERO;
		for (int i=x;i<=1000;++i) result=result.add(getCombinatorial(1000,i));
		Fraction frac=new Fraction(result,BigIntegerUtils.pow(2,1000));
		System.out.println(frac.asBigDecimal().toString());
	}
}
