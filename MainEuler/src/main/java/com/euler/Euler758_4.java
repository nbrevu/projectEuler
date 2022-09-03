package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler758_4 {
	private final static long MOD=1_000_000_007l;
	private final static long LIMIT=1000l;
	
	private static class ModularCalculator	{
		private final long mod;
		private final LongLongMap powersOfTwo;
		private final LongLongMap inverses;
		public ModularCalculator(long mod)	{
			this.mod=mod;
			powersOfTwo=HashLongLongMaps.newMutableMap();
			inverses=HashLongLongMaps.newMutableMap();
		}
		private long doCalculatePower(long exp)	{
			return EulerUtils.expMod(2l,exp,mod);
		}
		public long getPowerOfTwo(long exp)	{
			return powersOfTwo.computeIfAbsent(exp,this::doCalculatePower);
		}
		private long doCalculateInverse(long n)	{
			return EulerUtils.modulusInverse(n,mod);
		}
		public long getInverse(long n)	{
			return inverses.computeIfAbsent(n,this::doCalculateInverse);
		}
		private long makePositive(long n)	{
			return ((n%mod)+mod)%mod;
		}
		public long specialEuclidAlgorithm(long a,long b)	{
			// Assumes that a and b are coprime (ends when r=1), and a>b. Undefined results (probably an infinite loop or a divide by 0) happen otherwise.
			long oldR=a;
			long r=b;
			long oldS=1l;
			long s=0l;
			long oldT=0l;
			long t=1l;
			boolean isOddStep=false;
			for (;;)	{
				isOddStep=!isOddStep;
				long q=oldR/r;
				long newR=oldR%r;
				long qq=getPowerOfTwo(newR);
				if (q>1)	{
					long num=getPowerOfTwo(q*r)-1;
					long den=getPowerOfTwo(r)-1;
					long factor=num*getInverse(den)%mod;
					qq=(qq*factor)%mod;
				}
				oldR=r;
				r=newR;
				long newS=makePositive(oldS-qq*s);
				oldS=s;
				s=newS;
				long newT=makePositive(oldT-qq*t);
				oldT=t;
				t=newT;	//'s eye. Ha. Ha. Ha.
				if (newR==1l)	{
					/*
					 * If "isOddStep" is true, then S is positive and T is negative.
					 * And if "isOddStep" is false, then S is negative and T is positive.
					 * 
					 * In the first case, the best solution comes from S-T.
					 * In the second case, the best solution comes from T-S.
					 * If we call this "N", then the number we need to return is 2(N-1).
					 */
					long n=isOddStep?(s-t):(t-s);
					long result=2*(n-1);
					return makePositive(result);
				}
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0l;
		ModularCalculator calculator=new ModularCalculator(MOD);
		long[] primePowers=Primes.listLongPrimes(LIMIT).stream().mapToLong((Long in)->LongMath.pow(in.longValue(),5)).toArray();
		int N=primePowers.length;
		for (int i=0;i<N;++i) for (int j=i+1;j<N;++j) result+=calculator.specialEuclidAlgorithm(primePowers[j],primePowers[i]);
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
