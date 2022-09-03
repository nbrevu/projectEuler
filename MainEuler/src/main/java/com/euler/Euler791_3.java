package com.euler;

public class Euler791_3 {
	private final static int LIMIT=1000;
	private final static long MOD=433494437l;
	
	/*
	Elapsed 3730.2181211 seconds.
	11847639773754766842560404
	331832048
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long l2=LIMIT*2;
		long result=0;
		long maxA=(long)Math.floor(Math.sqrt(6*LIMIT));
		for (long a=-1;maxA+a>=0;--a)	{
			long a2=a*a;
			long maxB=-(a/3);
			for (long b=a;b<=maxB;b+=2)	{
				long b2=b*b;
				long maxC=-(a+b)/2;
				for (long c=b;c<=maxC;c+=2)	{
					long d=-(a+b+c);
					long x=(a2+b2+c*c+d*d)/4;
					if (((x+a)>0)&&((x+d)<=l2)) result=(result+2*x)%MOD;
					// if ((x+a)<=0) System.out.println(String.format("%d, %d, %d, %d.",x+a,x+b,x+c,x+d));
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
