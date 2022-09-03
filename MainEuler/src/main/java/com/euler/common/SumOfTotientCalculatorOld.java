package com.euler.common;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class SumOfTotientCalculatorOld {
	// Credits to daniel.is.fischer from Project Euler 73's overview doc.
	/*
	 * ACHTUNG, ESTA FUNCIÓN NO HACE LO QUE YO CREÍA QUE HACE. El nombre es incorrecto :(.
	 */
	private final BigInteger[] rsmall;
	private final BigInteger[] rlarge;
	private final long N;
	private final long K;
	private final long M;
	public SumOfTotientCalculatorOld(long n)	{
		N=n;
		K=LongMath.sqrt(n/2,RoundingMode.DOWN);
		M=n/(2*K+1);
		rsmall=new BigInteger[(int)(M+1)];
		rlarge=new BigInteger[(int)K];
	}
	private BigInteger calculateF(long n)	{
		long q=n/6;
		long r=n%6;
		BigInteger f=BigInteger.valueOf(q).multiply(BigInteger.valueOf(3*q-2+r));
		if (r==5) f=f.add(BigInteger.ONE);
		return f;
	}
	private void calculateR(long n)	{
		long s=LongMath.sqrt(n/2,RoundingMode.DOWN);
		BigInteger count=calculateF(n).subtract(calculateF(n/2));
		long m=5;
		long k=(n-5)/10;
		while (k>=s)	{
			long nextK=(n/(m+1)-1)/2;
			count=count.subtract(BigInteger.valueOf(k-nextK).multiply(rsmall[(int)m]));
			k=nextK;
			++m;
		}
		while (k>0)	{
			m=n/(2*k+1);
			if (m<=M) count=count.subtract(rsmall[(int)m]);
			else count=count.subtract(rlarge[(int)(((N/m)-1)/2)]);
			--k;
		}
		if (n<=M) rsmall[(int)n]=count;
		else rlarge[(int)(((N/n)-1)/2)]=count;
	}
	public BigInteger getSumOfTotients()	{
		for (long n=5;n<=M;++n) calculateR(n);
		for (long j=K-1;j>=0;--j) calculateR(N/(2*j+1));
		return rlarge[0];
	}
}
