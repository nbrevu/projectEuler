package com.euler;

import com.google.common.math.IntMath;

public class Euler628 {
	private static int N=IntMath.pow(10,8);
	private static long MOD=1008691207l;
	
	private static long[] getFactorials(int in,long mod)	{
		long[] result=new long[in+1];
		result[0]=1l;
		for (int i=1;i<=in;++i) result[i]=(result[i-1]*i)%mod;
		return result;
	}
	
	private static long getResult(int in,long mod)	{
		long[] factorials=getFactorials(in,mod);
		long result=factorials[in]-1;
		for (int i=1;i<in;++i)	{
			result-=2*factorials[i];
			result+=i*factorials[in-1-i];
			result%=mod;
		}
		if (result<0) result+=mod;
		return result;
	}
	
	public static void main(String[] args)	{
		long result=getResult(N,MOD);
		System.out.println(result);
	}
}
