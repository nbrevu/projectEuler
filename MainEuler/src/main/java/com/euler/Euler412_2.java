package com.euler;

import com.euler.common.EulerUtils;

public class Euler412_2 {
	// https://en.wikipedia.org/wiki/Young_tableau#Dimension_of_a_representation
	// https://oeis.org/A039622
	// https://arxiv.org/pdf/0810.4701
	private final static long MOD=76543217l;
	private final static long N1=10000l;
	
	public static void main(String[] args)	{
		long n2=N1/2;
		long result=EulerUtils.factorialMod((N1*N1)-(n2*n2),MOD);
		for (long i=1;i<=n2;++i)	{
			long power1=EulerUtils.expMod(i,2*i,MOD);
			long term1=EulerUtils.modulusInverse(power1,MOD);
			long power2=EulerUtils.expMod(i+N1,i,MOD);
			long term2=EulerUtils.modulusInverse(power2,MOD);
			result=(result*term1)%MOD;
			result=(result*term2)%MOD;
		}
		for (long i=1+n2;i<N1;++i)	{	// No need to include N1 in the loop: the term is 1.
			long power1=EulerUtils.expMod(i,2*(N1-i),MOD);
			long term1=EulerUtils.modulusInverse(power1,MOD);
			long power2=EulerUtils.expMod(i+N1,N1-i,MOD);
			long term2=EulerUtils.modulusInverse(power2,MOD);
			result=(result*term1)%MOD;
			result=(result*term2)%MOD;
		}
		System.out.println(result);
	}
}
