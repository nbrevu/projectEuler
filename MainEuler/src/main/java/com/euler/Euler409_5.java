package com.euler;

public class Euler409_5 {
	public static void main(String[] args)	{
		int n=7;
		long e=(1l<<n)-1;
		long[] w=new long[n+1];
		long[] l=new long[n+1];
		w[0]=1l;
		l[0]=0l;
		w[1]=e;
		l[1]=0l;
		long f=e*(e-1);
		w[2]=f;
		l[2]=0;
		for (int i=3;i<=n;++i)	{
			f*=e+1-i;
			l[i]=w[i-1]-(i-1)*(e+2-i)*l[i-2];
			w[i]=f-l[i];
		}
		System.out.println(w[n]);
	}
}
