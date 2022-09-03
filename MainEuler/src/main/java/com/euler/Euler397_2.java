package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler397_2 {
	private final static long K=10;
	private final static long X=100;
	
	private static void displayRectangle(long a,long b,long c,long k)	{
		long x=-a+2*b+3*c;
		long y=b+c;
		long delta2=x*x-8*y*y;
		if (delta2<0) throw new IllegalStateException(":(");
		long delta=LongMath.sqrt(delta2,RoundingMode.UNNECESSARY);
		if (((x-delta)%2)!=0) throw new IllegalStateException(":(");
		long p=x+delta;
		long q=x-delta;
		if (p*q!=8*y*y) throw new IllegalStateException(":(");
		long diff=c-a;
		if (((diff-delta)%2)!=0) throw new IllegalStateException(":(");
		if ((2*k!=Math.abs(diff-delta))&&(2*k!=diff+delta)) throw new IllegalStateException(":(");
		System.out.println(String.format("x=%d, y=%d, p=%d, q=%d, delta=%d, c-a=%d.",x,y,p,q,delta,c-a));
		if ((y==0)&&(p!=2*k)) throw new IllegalStateException(":(");
	}
	
	public static void main(String[] args)	{
		int result=0;
		int countA=0;
		int countB=0;
		int countC=0;
		for (long k=1;k<=K;++k) for (long a=-X;a<=X;++a) for (long b=a+1;b<=X;++b) for (long c=b+1;c<=X;++c)	{
			long ax=k*a;
			long ay=a*a;
			long bx=k*b;
			long by=b*b;
			long cx=k*c;
			long cy=c*c;
			long abx=bx-ax;
			long bcx=cx-bx;
			long acx=cx-ax;
			long aby=by-ay;
			long bcy=cy-by;
			long acy=cy-ay;
			long ab2=abx*abx+aby*aby;
			long bc2=bcx*bcx+bcy*bcy;
			long ac2=acx*acx+acy*acy;
			long abbc=abx*bcx+aby*bcy;
			long bcac=bcx*acx+bcy*acy;
			long abac=abx*acx+aby*acy;
			long abbc2=abbc*abbc;
			long bcac2=bcac*bcac;
			long abac2=abac*abac;
			int counts=0;
			if ((abbc<0)&&(abbc2*2==ab2*bc2))	{	// Note the sign!
				System.out.println(String.format("Type B triangle: k=%d, a=%d, b=%d, c=%d.",k,a,b,c));
				displayRectangle(a,b,c,k);
				++countB;
				++counts;
			}
			if ((bcac>0)&&(bcac2*2==bc2*ac2))	{
				System.out.println(String.format("Type C triangle: k=%d, a=%d, b=%d, c=%d.",k,a,b,c));
				displayRectangle(a,c,b,k);
				++countC;
				++counts;
			}
			if ((abac>0)&&(abac2*2==ab2*ac2))	{
				System.out.println(String.format("Type A triangle: k=%d, a=%d, b=%d, c=%d.",k,a,b,c));
				displayRectangle(b,a,c,k);
				++countA;
				++counts;
			}
			if (counts>2) throw new IllegalArgumentException("Mira, no sé qué has hecho, pero está MAL.");
			if (counts>1) System.out.println(":O");
			if (counts>0) ++result;
		}
		System.out.println(result);
		System.out.println(String.format("Counts for: A=%d, B=%d, C=%d.",countA,countB,countC));
	}
}
