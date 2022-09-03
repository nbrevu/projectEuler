package com.euler;

public class Euler791_5 {
	private final static long LIMIT=100000000;
	private final static long MOD=433494437l;
	
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
				long n_=(maxC-minC)/2;
				long n=1+n_;
				long sum=n*((k+s2)/2)+s*n*(minC+n_)+n*minC*minC+2*n*n_*minC+2*(n_*(1+n_*(3+2*n_)))/3;
				result=(result+sum)%MOD;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
