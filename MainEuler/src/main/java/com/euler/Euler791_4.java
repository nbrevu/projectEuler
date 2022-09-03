package com.euler;

public class Euler791_4 {
	private final static int LIMIT=1000;
	private final static long MOD=433494437l;
	
	/*
	Elapsed 2602.887524301 seconds.
	404890862
	404890862
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long deltaConst=LIMIT*16+4;
		long result=-12;	// Offset for some spurious solutions where A<=0 (invalid).
		long maxA=(long)Math.floor(Math.sqrt(6*LIMIT));
		for (long a=-1;maxA+a>=0;--a)	{
			long a2=a*a;
			long maxB=-(a/3);
			for (long b=a;b<=maxB;b+=2)	{
				long s=a+b;
				long s2=s*s;
				long k=a2+b*b;
				long delta=deltaConst+4*s-s2-2*k;
				if (delta<0) continue;
				double offset=1-0.5*s;
				double sDelta=0.5*Math.sqrt(delta);
				long maxC1=-(a+b)/2;
				long minC2=(long)Math.ceil(offset-sDelta);
				long maxC2=(long)Math.floor(offset+sDelta);
				if (((minC2-b)%2)!=0) ++minC2;
				long minC=Math.max(b,minC2);
				long maxC=Math.min(maxC1,maxC2);
				if (maxC<minC) continue;
				for (long c=minC;c<=maxC;c+=2)	{
					long d=-(a+b+c);
					long x=(k+c*c+d*d)/2;
					result=(result+x)%MOD;
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
