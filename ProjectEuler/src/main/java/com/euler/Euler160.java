package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Timing;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntLongMap;
import com.koloboke.collect.map.hash.HashIntLongMaps;

public class Euler160 {
	private final static int MOD=IntMath.pow(10,5);
	private final static long N=LongMath.pow(10l,12);
	
	private static long[] getPartialFactorials(int in)	{
		long[] result=new long[in+1];
		result[0]=1;
		IntLongMap cache=HashIntLongMaps.newMutableMap();
		cache.put(0,1l);
		int k=0;
		long n=1;
		for (int i=1;i<=in;++i)	{
			if (i%5==0) --k;
			else if (i%2==0)	{
				n*=(i/2);
				n%=in;
				++k;
			}	else n=(n*i)%in;
			long pow2=cache.computeIfAbsent(k,(int p)->(2*cache.get(p-1))%in);
			result[i]=(n*pow2)%in;
		}
		return result;
	}
	
	private static long f(long n,long[] partialFactorials,int in)	{
		if (n<5) return partialFactorials[(int)n];
		long factor1=f(n/5,partialFactorials,in);
		long factor2=EulerUtils.expMod(partialFactorials[in],n/in,in);
		long factor3=partialFactorials[(int)(n%in)];
		return (factor1*factor2*factor3)%in;
	}
	
	private static long solve()	{
		long[] partialFactorials=getPartialFactorials(MOD);
		return f(N,partialFactorials,MOD);
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler160::solve);
	}
}
