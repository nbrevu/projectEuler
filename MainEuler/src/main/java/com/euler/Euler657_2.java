package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler657_2 {
	private final static int A=IntMath.pow(10,7);
	private final static long L=LongMath.pow(10l,12);
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		/*
		 * Really? That simple?
		 * The only difficulty is in the combinatorial number, which can be calculated iteratively using inverses. Yay?
		 */
		long[] inverses=EulerUtils.calculateModularInverses2(A-1,MOD);
		long combi=1;	// Initially C(A,A).
		boolean add=true;
		long result=0l;
		for (int a=A-1;a>=2;--a,add=!add)	{
			long strings=((EulerUtils.expMod(a,L+1,MOD)-1)*inverses[a-1])%MOD;
			/*
			 * We have combi=C(A,a+1) = A!/[(a+1)!(A-a-1)!].
			 * We want combiX=C(A,a) = A!/[a!(A-a)!]
			 * combiX=combi*(a+1)/(A-a).
			 */
			combi*=a+1;
			combi%=MOD;
			combi*=inverses[A-a];
			combi%=MOD;
			long toAdd=(strings*combi)%MOD;
			result+=add?toAdd:(MOD-toAdd);
		}
		// Now the special case a=1: strings=L+1, combi=A.
		{
			long strings=(L+1)%MOD;
			long toAdd=(strings*A)%MOD;
			result+=add?toAdd:(MOD-toAdd);
		}
		// Finally the special case a=0, strings=1, combi=1. Note the inverted toAdd!
		result+=add?-1:1;
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
