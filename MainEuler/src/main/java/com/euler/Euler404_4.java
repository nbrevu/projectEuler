package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler404_4 {
	private final static long N=LongMath.pow(10l,17);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		long maxM1=LongMath.sqrt(LongMath.sqrt(N,RoundingMode.DOWN),RoundingMode.DOWN);
		long maxM2=LongMath.sqrt(LongMath.sqrt((16*N)/25,RoundingMode.DOWN),RoundingMode.DOWN);
		long maxM=Math.max(maxM1,maxM2);
		for (long m=3;m<=maxM;++m)	{
			long m2=m*m;
			long m3=m2*m;
			long m4=m3*m;
			{
				long minN=(m%2)+1;
				long maxN=m/3;
				for (long n=minN;n<=maxN;n+=2) if (EulerUtils.areCoprime(m,n))	{
					long n2=n*n;
					long cond=m2-m*n-n2;
					if ((cond%5)==0) continue;
					long n3=n2*n;
					long n4=n3*n;
					long a=m4+3*m3*n-6*m2*n2-3*m*n3+n4;
					if (a<=N) result+=N/a;
				}
			}
			if (m<=maxM2)	{
				long minN=(m>>1)+1;
				if (((m-minN)%2)==0) ++minN;
				long maxN=m-1;
				for (long n=minN;n<=maxN;n+=2) if (EulerUtils.areCoprime(m,n))	{
					long n2=n*n;
					long cond=n2-m2+4*m*n;
					if ((cond%5)==0) continue;
					long n3=n2*n;
					long n4=n3*n;
					long a=-m4+3*m3*n+6*m2*n2-3*m*n3-n4;
					if (a<=N) result+=N/a;
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
