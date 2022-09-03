package com.euler;

import java.math.BigInteger;

public class Euler791_6 {
	private final static long LIMIT=10000000000l;
	private final static long MOD=433494437l;
	private final static BigInteger THREE=BigInteger.valueOf(3l);
	
	/*-
	Elapsed 3.4675178000000004 seconds.
	118472070561036500864
	404890862
	
	Elapsed 34.1413138 seconds.
	37465197993602231350606
	73959854
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long deltaConst=LIMIT*16+4;
		BigInteger result=BigInteger.valueOf(-12);	// Offset for some spurious solutions where A<=0 (invalid).
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
				BigInteger bigN_=BigInteger.valueOf(n_);
				BigInteger bigN=bigN_.add(BigInteger.ONE);
				BigInteger bigK=BigInteger.valueOf(k);
				BigInteger bigS=BigInteger.valueOf(s);
				BigInteger bigS2=BigInteger.valueOf(s2);
				BigInteger bigMinC=BigInteger.valueOf(minC);
				result=result.add(bigN.multiply(bigK.add(bigS2).shiftRight(1))).add(bigS.multiply(bigN.multiply(bigMinC.add(bigN_))))
						.add(bigN.multiply(bigMinC.multiply(bigMinC))).add(BigInteger.TWO.multiply(bigN).multiply(bigN_).multiply(bigMinC))
						.add(bigN_.multiply(BigInteger.ONE.add(bigN_.multiply(THREE.add(bigN_.shiftLeft(1))))).divide(THREE).shiftLeft(1));
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
		System.out.println(result.mod(BigInteger.valueOf(MOD)));
	}
}
