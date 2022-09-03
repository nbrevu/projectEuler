package com.euler;

public class Euler397 {
	private final static long K=1;
	private final static long X=10;
	
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
				++countB;
				++counts;
			}
			if ((bcac>0)&&(bcac2*2==bc2*ac2))	{
				System.out.println(String.format("Type C triangle: k=%d, a=%d, b=%d, c=%d.",k,a,b,c));
				++countC;
				++counts;
			}
			if ((abac>0)&&(abac2*2==ab2*ac2))	{
				System.out.println(String.format("Type A triangle: k=%d, a=%d, b=%d, c=%d.",k,a,b,c));
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
