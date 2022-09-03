package com.euler;

public class Euler409_6 {
	public static void main(String[] args)	{
		int n=7;
		long e=(1l<<n)-1;
		long lMinus1=0l;
		long f=e*(e-1);
		long l=0;
		long w=f;
		for (int i=3;i<=n;++i)	{
			long lMinus2=lMinus1;
			lMinus1=l;
			long wMinus1=w;
			f*=e+1-i;
			l=wMinus1-(i-1)*(e+2-i)*lMinus2;
			w=f-l;
		}
		System.out.println(w);
	}
}
