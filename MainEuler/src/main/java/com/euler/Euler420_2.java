package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler420_2 {
	private final static long N=10000000;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] firstPrimes=Primes.firstPrimeSieve(N);
		long result=0;
		for (long t1=1;;++t1)	{
			long t=t1*t1;
			if (t>=N) break;
			for (long t2=t1+2;t2<=N;t2+=2)	{
				long tx=t2*t2;
				if (((t+tx)/2)>=N) break;
				long s=(tx-t)/4;
				long g=EulerUtils.gcd(t1,t2);
				long l=(t1*t2)/g;
				long ll=l*l;
				long mod2=t2+((-2*s)%t2);
				long x0=g*EulerUtils.solveChineseRemainder(0,t1/g,mod2/g,t2/g);
				if (x0==0) x0=l;
				for (long x=x0;x<t;x+=l)	{
					long y=t-x;
					long num=s*t+x*y;
					long bc=num/ll;
					result+=DivisorHolder.getFromFirstPrimes(bc,firstPrimes).getAmountOfDivisors();
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
