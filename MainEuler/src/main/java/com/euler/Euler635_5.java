package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler635_5 {
	private final static long LIMIT=100_000_000l;
	private final static long MOD=1_000_000_009l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] primes=Primes.listLongPrimesAsArraySkippingFirstOnes(LIMIT,1);
		long result=8l;	// Cases for n=2 summed separately.
		long[] inverses=EulerUtils.calculateModularInverses2(1+2*(int)primes[primes.length-1],MOD);
		long combi2=6;	// Binom(4,2).
		long combi3=15;	// Binom(6,2).
		long lastPrime=2l;
		/*
		 * S_2(p) = (B(2*p,p)+2*(p-1))/p.
		 * S_3(p) = (B(3*p,p)+3*(p-1))/p.
		 * Obviously the combinatorials must be updated inside the iteration.
		 * Let's say we have B(2*a,a) and we want to calculate B(2*(a+1),a+1).
		 * We apply the factor (2*a+2)*(2*a+1) to the numerator, and (a+1)^2 to the denominator.
		 * Therefore we need the inverse of (a+1).
		 * 
		 * Now let's say we have B(3*a,a) and we want to calculate B(3*(a+1),a+1).
		 * We apply the factor (3*a+3)*(3*a+2)*(3*a+1) to the numerator, and (a+1)*(2*a+2)*(2*a+1).
		 * Therefore we need inverses up to (2*a+2).
		 */
		for (long p:primes)	{
			// Update the combinatorial numbers.
			for (long q=1+lastPrime;q<=p;++q)	{
				long q2=q+q;
				long q3=q2+q;
				combi2*=q2-1;
				combi2%=MOD;
				combi2*=q2;
				combi2%=MOD;
				long invq=inverses[(int)q];
				combi2*=invq;
				combi2%=MOD;
				combi2*=invq;
				combi2%=MOD;
				combi3*=q3-2;
				combi3%=MOD;
				combi3*=q3-1;
				combi3%=MOD;
				combi3*=q3;
				combi3%=MOD;
				combi3*=invq;
				combi3%=MOD;
				combi3*=inverses[(int)q2-1];
				combi3%=MOD;
				combi3*=inverses[(int)q2];
				combi3%=MOD;
			}
			long addend=combi2+combi3+5*(p-1);
			addend*=inverses[(int)p];
			addend%=MOD;
			result+=addend;
			lastPrime=p;
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
