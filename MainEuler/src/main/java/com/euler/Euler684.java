package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler684 {
	private final static int MAX_F=90;
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static long getS(long n,long mod)	{
		long a=n/9;
		long b=n%9;
		long factor=(((b+2)*(b+1))/2)+5l;
		long pow10=EulerUtils.expMod(10l,a,mod);
		long subtract=n+6;
		long add=mod-(subtract%mod);
		return (factor*pow10+add)%mod;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		long fP2=0;
		long fP=1;
		for (int i=2;i<=MAX_F;++i)	{
			long f=fP+fP2;
			result+=getS(f,MOD);
			fP2=fP;
			fP=f;
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
