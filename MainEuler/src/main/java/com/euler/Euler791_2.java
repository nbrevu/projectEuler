package com.euler;

import java.util.Locale;

public class Euler791_2 {
	private final static int LIMIT=100000000;
	
	public static void main(String[] args)	{
		int counter=0;
		long sum=0;
		long currMaxDiff=4;
		long nextChange=10;
		long jitter=10;
		long actualDiff=currMaxDiff+jitter;
		for (long a=1;a<=LIMIT;++a)	{
			if (a>=nextChange)	{
				currMaxDiff*=4;
				nextChange*=10;
				actualDiff=currMaxDiff+jitter;
			}
			long a2=a*a;
			long limA=Math.min(LIMIT,a+actualDiff);
			for (long b=a;b<=limA;++b)	{
				long sab=a+b;
				long sab2=a2+b*b;
				for (long c=b;c<=limA;++c)	{
					long sabc=sab+c;
					long sabc2=sab2+c*c;
					long minD=c+((a+b)%2);
					for (long d=minD;d<=limA;d+=2)	{
						long hs=(sabc+d)/2;
						long s2=sabc2+d*d;
						if ((hs*hs)+hs==s2)	{
							System.out.println(String.format(Locale.UK,"a=%d, b=%d, c=%d, d=%d.",a,b,c,d));
							++counter;
							sum+=hs;
						}
					}
				}
			}
		}
		System.out.println(String.format("Found %d cases, summing up to %d.",counter,2*sum));
	}
}
