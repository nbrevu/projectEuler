package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler805_10 {
	private final static long N=200;
	private final static long MOD=1_000_000_007l;
	
	private static class ValueCalculator	{
		private final static long[] POSSIBLE_DIVS=new long[] {9,3};
		private final long[] firstPrimes;
		private final long mod;
		private final long inv3;
		private final long inv9;
		public ValueCalculator(long n,long mod)	{
			firstPrimes=Primes.firstPrimeSieve(10l*n*n*n);
			this.mod=mod;
			inv3=EulerUtils.modulusInverse(3l,mod);
			inv9=(inv3*inv3)%mod;
		}
		private static int maxPower3(long in)	{
			int result=0;
			while ((in%3)==0)	{
				++result;
				in/=3;
			}
			return result;
		}
		private long calculateCycleLength(long q)	{
			long totient=DivisorHolder.getFromFirstPrimes(q,firstPrimes).getTotient();
			long[] divs=DivisorHolder.getFromFirstPrimes(totient,firstPrimes).getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(divs);
			for (long d:divs) if (EulerUtils.expMod(10l,d,q)==1) return d;
			throw new IllegalStateException();
		}
		public long calculateValue(long num,long den)	{
			long q=10*den-num;
			if (q<=0) return 0;
			while ((q%2)==0) q/=2;
			while ((q%5)==0) q/=5;
			if ((q<=den)||(q<=num)) return 0;
			for (long d:POSSIBLE_DIVS) if ((q%d)==0)	{
				long qd=q/d;
				if ((qd>den)&&(qd>num))	{
					q=qd;
					break;
				}
			}
			long firstDigit=((10*den)/q);
			boolean firstDigitIs9=(firstDigit==9);
			boolean firstFigitIs3Or6=((firstDigit==3)||(firstDigit==6));
			long nDigits=calculateCycleLength(q);
			long pow10=EulerUtils.expMod(10l,nDigits,mod)-1;
			long result=(pow10*den)%mod;
			result=(result*EulerUtils.modulusInverse(q,mod))%mod;
			if (firstDigitIs9||firstFigitIs3Or6)	{
				int numThrees=2+maxPower3(nDigits)-maxPower3(q)+maxPower3(den);
				int minNeededThrees=maxPower3(num);
				if (firstDigitIs9&&(minNeededThrees<=numThrees-2)) return (result*inv9)%mod;
				else if (minNeededThrees<=numThrees-1) return (result*inv3)%mod;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ValueCalculator calcu=new ValueCalculator(N,MOD);
		long result=0l;
		for (long u=1;u<=N;++u)	{
			long num=u*u*u;
			for (long v=1;v<=N;++v) if (EulerUtils.areCoprime(u,v))	{
				long den=v*v*v;
				result+=calcu.calculateValue(num,den);
			}
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
