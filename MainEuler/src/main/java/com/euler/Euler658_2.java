package com.euler;

import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.google.common.math.LongMath;

public class Euler658_2 {
	private static long calculateBigSum(int n,int p,CombinatorialNumberCache combis)	{
		long result=0;
		for (int i=1;i<=p;++i) result+=calculateSumOfCombiPowers(n,i,combis);
		return result;
	}
	
	private static long calculateSumOfCombiPowers(int n,int p,CombinatorialNumberCache combis)	{
		long result=0;
		for (int i=1;i<=n;++i) result+=LongMath.pow(i,p)*combis.get(n,i);
		return result;
	}
	
	public static void main(String[] args)	{
		int maxN=15;
		int power=5;
		CombinatorialNumberCache combis=new CombinatorialNumberCache(maxN);
		//for (int i=1;i<=maxN;++i) System.out.println(String.format("sum(C(%d,x)*x^%d,x,0,%d)=%d.",i,power,i,calculateSumOfCombiPowers(i,power,combis)));
		for (int i=1;i<=maxN;++i) System.out.println(String.format("sum(C(%d,x)*x^%d,x,0,%d)=%d.",i,power,i,calculateBigSum(i,power,combis)));
	}
}
