package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class Euler445_2 {
	/*
	 * Here's the deal: we need to calculate R(n) for a bunch of very large n. Now, n is very large BUT we have the means to get its prime
	 * decomposition. In fact it's useless to calculate n, since it's going to be stupidly big (like nchoosek(10^7,5*10^6) stupidly big).
	 * 
	 * So, we need to find pairs (a,b) so that (a^2-a) and (ab) are congruent with 0 (mod n). This also looks stupidly impossible even if you
	 * make intensive usage of CRT. Of course there is a more simple pattern, which  is the following one: for each ordered pair (p,q) with p*q=n
	 * AND (p,q) coprime, there is exactly one value of a that fits the bill, and p values of b. This is great because it suddenly simplifies
	 * the pattern to the SUM of all p minus the COUNT of all p. To make things a bit more complex, the fact that p and q must be coprime (in
	 * other words, we forget q and we just say n/p is coprime with p) implies that p can only contain the prime factors of n raised to the
	 * exact same power they appear in n. Fortunately, it's easy to modify one of the classical formulas to get the result we want.
	 * 
	 * So, let's say that n=p1^e1 * p2^e2 * p3^e3 * ... pk^ek. Now, instead of treating prime factors as such, we define qi=pi^ei so that
	 * n=q1*q2*q3...qk. Then, the amount of cases is 2^k and the sum is (q1+1)*(q2+1)*...*(qk+1). But this scheme counts the cases with a=n,
	 * which are not valid (the case a=1, b=0, however, IS valid, and it's counted properly). So, in the end, the result is:
	 * R(n) = (q1+1)*(q2+1)*...*(qk+1) - q1*q2*...qk . All of this must be done modulo a certain number (1e9+7, as usual), which is to be
	 * expected since otherwise the result will take enormous amounts of memory to calculate.
	 * 
	 * Oh, by the way, nchoosek(M,a) = nchoosek(M,M-a) so this reduces the calculation time in half. Weeee!
	 * 
	 * However the factorials don't fit in memory so this is still basically quadratic in N, which is unbearable for N=1e7...
	 * The combinatorial numbers should be generated intelligently, from the previous one, I guess?
	 */
	private final static int LIMIT=IntMath.pow(10,7);
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static class DivisorCalculator	{
		private final long[] firstPrimes;
		public DivisorCalculator(long max)	{
			firstPrimes=Primes.firstPrimeSieve(max);
		}
		public DivisorHolder getDivisors(int in)	{
			if (in<2) return new DivisorHolder();
			else return DivisorHolder.getFromFirstPrimes(in,firstPrimes);
		}
	}
	
	private static long calculateR(DivisorHolder decomposition,long mod)	{
		LongIntMap factors=decomposition.getFactorMap();
		long prodBase=1;
		long prodPlus1=1;
		for (LongIntCursor cursor=factors.cursor();cursor.moveNext();)	{
			long power=EulerUtils.expMod(cursor.key(),cursor.value(),mod);
			prodBase=(prodBase*power)%mod;
			prodPlus1=(prodPlus1*(power+1))%mod;
		}
		long result=prodPlus1-prodBase;
		result%=mod;
		if (result<0) result+=mod;
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int l2=LIMIT/2;
		if (l2+l2!=LIMIT) System.out.println("Entschuldigung. Hier kannst du nur gerade Zahlen benutzen!!!!!");
		DivisorCalculator divisors=new DivisorCalculator(LIMIT);
		DivisorHolder combinatorial=divisors.getDivisors(LIMIT);
		long result=calculateR(combinatorial,MOD);
		for (int i=2;i<l2;++i)	{
			combinatorial=DivisorHolder.divide(DivisorHolder.combine(combinatorial,divisors.getDivisors(LIMIT+1-i)),divisors.getDivisors(i));
			result+=calculateR(combinatorial,MOD);
		}
		result*=2;	// All but the last numbers must be counted twice.
		combinatorial=DivisorHolder.divide(DivisorHolder.combine(combinatorial,divisors.getDivisors(LIMIT+1-l2)),divisors.getDivisors(l2));
		result+=calculateR(combinatorial,MOD);
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
