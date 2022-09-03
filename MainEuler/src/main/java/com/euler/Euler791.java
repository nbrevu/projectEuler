package com.euler;

import java.util.Locale;

public class Euler791 {
	private final static int LIMIT=1000;
	
	public static void main(String[] args)	{
		int counter=0;
		long sum=0;
		long maxBa=0;
		long maxCa=0;
		long maxCb=0;
		long maxDa=0;
		long maxDb=0;
		long maxDc=0;
		for (long a=1;a<=LIMIT;++a)	{
			long a2=a*a;
			for (long b=a;b<=LIMIT;++b)	{
				long sab=a+b;
				long sab2=a2+b*b;
				for (long c=b;c<=LIMIT;++c)	{
					long sabc=sab+c;
					long sabc2=sab2+c*c;
					for (long d=c;d<=LIMIT;++d)	{
						long s=sabc+d;
						long s2=sabc2+d*d;
						double m=s/4.0;	// Exact thanks to how double representation works.
						double val=4*m*m+2*m;
						if (val==s2)	{
							maxBa=Math.max(maxBa,b-a);
							maxCa=Math.max(maxCa,c-a);
							maxCb=Math.max(maxCb,c-b);
							maxDa=Math.max(maxDa,d-a);
							maxDb=Math.max(maxDb,d-c);
							maxDc=Math.max(maxDc,d-b);
							System.out.println(String.format(Locale.UK,"a=%d, b=%d, c=%d, d=%d (avg=%.1f).",a,b,c,d,m));
							++counter;
							sum+=s;
						}
					}
				}
			}
		}
		System.out.println(String.format("Found %d cases, summing up to %d.",counter,sum));
		System.out.println(String.format("Maximum differences: b-a=%d, c-a=%d, c-b=%d, d-a=%d, d-b=%d, d-c=%d.",maxBa,maxCa,maxCb,maxDa,maxDb,maxDc));
	}
}
