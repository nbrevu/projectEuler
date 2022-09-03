package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler423_2 {
	private final static int IN=5*IntMath.pow(10,7);
	private final static long MOD=LongMath.pow(10l,9)+7l;
	
	private static int[] getM(int max)	{
		boolean[] composites=Primes.sieve(max);
		int[] result=new int[1+max];
		result[0]=0;
		int index=1;
		for (int i=2;i<=max;++i) if (composites[i])	{
			result[index]=i-1;
			++index;
		}
		Arrays.fill(result,index,max+1,max);
		return result;
	}
	
	private static long prod(long a,long b,long mod)	{
		long result=1l;
		for (long p=a;p<=b;++p) result=(result*p)%mod;
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] mValues=getM(IN);
		long exp5=1l;
		long combinatorial=mValues[1];	// (nchoosek(m,k) where m=m(1)=3 and k=1.
		long result=exp5*combinatorial;
		for (int k=2;k<=IN;++k)	{
			exp5=(exp5*5)%MOD;
			long numeratorFactor,denominatorFactor;
			if (mValues[k-1]==mValues[k])	{
				numeratorFactor=mValues[k]-k+1;
				denominatorFactor=k;
			}	else	{
				numeratorFactor=prod(mValues[k-1]+1,mValues[k],MOD);
				denominatorFactor=(k*prod(mValues[k-1]-k+2,mValues[k]-k,MOD))%MOD;
			}
			long denomInv=EulerUtils.modulusInverse(denominatorFactor,MOD);
			if (denomInv<0) denomInv+=MOD;
			long factor=(numeratorFactor*denomInv)%MOD;
			combinatorial=(combinatorial*factor)%MOD;
			result=(result+exp5*combinatorial)%MOD;
		}
		result=(6*result)%MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
