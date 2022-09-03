package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler602_2 {
	private final static int N=10000000;
	private final static int K=4000000;
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static long getCNK(int n,int k,long mod)	{
		long result=0l;
		long binomial=1;
		for (int i=0;i<=k;++i)	{
			long power=EulerUtils.expMod(k-i,n,mod);
			long value=(binomial*power)%mod;
			if ((i%2)==0) result+=value;
			else result-=value;
			result%=mod;
			/*
			 * Binomial update: we currently have A=nChooseK(n+1,i) and we want to calculate B=nChooseK(n+1,i+1).
			 * Since A=(n+1)!/[(n+1-i)!*i!] and B=(n+1)!/[(n-i)!(i+1)!], then B/A=[(n+1-i)!/(n-i)!]*[i!/(i+1)!]=(n+1-i)/(i+1).
			 * And since we are using modular arithmetic, this ius equivalent to multiplying by (n+1-i)*ModularInverse(i+1,mod).
			 */
			binomial*=n+1-i;
			binomial%=mod;
			binomial*=EulerUtils.modulusInverse(i+1,mod);
			binomial%=mod;
		}
		if (result<0) result+=mod;	// We want the modulus, not the remainder.
		return result;
	}
	
	public static void main(String[] args)	{
		System.out.println(getCNK(N,K,MOD));
	}
}
