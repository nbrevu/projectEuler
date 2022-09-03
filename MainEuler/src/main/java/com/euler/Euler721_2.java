package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler721_2 {
	private static long[] getFactorials(int upTo)	{
		long[] result=new long[1+upTo];
		result[0]=1l;
		for (int i=1;i<=upTo;++i) result[i]=i*result[i-1];
		return result;
	}
	
	private static long calculate(long a,int n)	{
		long[] factorials=getFactorials(n);
		long ceilSqrt=LongMath.sqrt(a,RoundingMode.UP);
		boolean isPerfectSquare=(ceilSqrt*ceilSqrt==a);
		long result=0;
		for (int i=0;i<=n/2;++i)	{
			long combinatorial=factorials[n]/(factorials[2*i]*factorials[n-2*i]);
			long power=LongMath.pow(ceilSqrt,n-2*i)*LongMath.pow(a,i);
			result+=combinatorial*power;
		}
		result*=2;
		if (!isPerfectSquare) --result;
		return result;
	}
	
	private static long calculateDirect(long a,int n)	{
		double s=Math.sqrt(a);
		double c=Math.ceil(s);
		double base=s+c;
		double power=Math.pow(base,n);
		double result=Math.floor(power);
		return (long)result;
	}
	
	public static void main(String[] args)	{
		for (int j=2;j<10;++j) for (int i=1;i<=10;++i) System.out.println(calculate(j,i)+" <==> "+calculateDirect(j,i));
	}
}
