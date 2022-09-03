package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler445_4 {
	/*
	 * Here's the deal: we need to calculate R(n) for a bunch of very large n. Now, n is very large BUT we have the means to get its prime
	 * decomposition. In fact it's useless to calculate n, since it's going to be stupidly big (like nchoosek(10^7,5*10^6) stupidly big).
	 * 
	 * So, we need to find pairs (a,b) so that (a^2-a) and (ab) are congruent with 0 (mod n). This also looks stupidly impossible even if you
	 * make intensive usage of CRT. Of course there is a more simple pattern, which is the following one: for each ordered pair (p,q) with p*q=n
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
	
	private static class ExtendedCombinatorial	{
		private static class PowAndValue	{
			public int power;
			public long value;
		}
		private final long mod;
		private final PowAndValue[] factors;
		private final int[] firstPrimes;
		private long prodBase;
		private long prodPlus1;
		private LongLongMap inverseCache;
		public ExtendedCombinatorial(int max,long mod)	{
			this.mod=mod;
			firstPrimes=Primes.firstPrimeSieve(max);
			factors=new PowAndValue[1+max];
			prodBase=1;
			prodPlus1=1;
			inverseCache=HashLongLongMaps.newMutableMap();
		}
		private long getInverse(long in)	{
			return inverseCache.computeIfAbsent(in,(long arg)->EulerUtils.modulusInverse(arg,mod));
		}
		public long calculateRAfterAddAndRemove(int toAdd,int toRemove)	{
			IntIntMap addedFactors=HashIntIntMaps.newMutableMap();
			addToMap(addedFactors,toAdd,1);
			addToMap(addedFactors,toRemove,-1);
			for (IntIntCursor cursor=addedFactors.cursor();cursor.moveNext();)	{
				int powerIncrease=cursor.value();
				if (powerIncrease==0) continue;
				int prime=cursor.key();
				PowAndValue existing=factors[prime];
				if (existing==null)	{
					long primePower=EulerUtils.expMod(prime,powerIncrease,mod);
					existing=new PowAndValue();
					existing.power=powerIncrease;
					existing.value=primePower;
					factors[prime]=existing;
					prodBase=(prodBase*primePower)%mod;
					prodPlus1=(prodPlus1*(primePower+1))%mod;
				}	else	{
					long oldPrimePower=existing.value;
					prodBase=(prodBase*getInverse(oldPrimePower))%mod;
					prodPlus1=(prodPlus1*getInverse(oldPrimePower+1))%mod;
					existing.power+=powerIncrease;
					if (existing.power==0) factors[prime]=null;
					else	{
						long primePower=EulerUtils.expMod(prime,existing.power,mod);
						prodBase=(prodBase*primePower)%mod;
						prodPlus1=(prodPlus1*(primePower+1))%mod;
						existing.value=primePower;
					}
				}
			}
			long result=prodPlus1-prodBase;
			result%=mod;
			if (result<0) result+=mod;
			return result;
		}
		private void addToMap(IntIntMap factors,int value,int increase)	{
			for (;;)	{
				int prime=firstPrimes[value];
				if (prime==0)	{
					factors.addValue(value,increase);
					return;
				}	else if (prime==1) return;
				factors.addValue(prime,increase);
				value/=prime;
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int l2=LIMIT/2;
		if (l2+l2!=LIMIT) System.out.println("Entschuldigung. Hier kannst du nur gerade Zahlen benutzen!!!!!");
		ExtendedCombinatorial combinatorial=new ExtendedCombinatorial(LIMIT,MOD);
		long result=combinatorial.calculateRAfterAddAndRemove(LIMIT,1);
		for (int i=2;i<l2;++i) result+=combinatorial.calculateRAfterAddAndRemove(LIMIT+1-i,i);
		result*=2;	// All but the last numbers must be counted twice.
		result+=combinatorial.calculateRAfterAddAndRemove(l2+1,l2);
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
