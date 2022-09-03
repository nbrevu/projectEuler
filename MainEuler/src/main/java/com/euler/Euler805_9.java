package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler805_9 {
	private final static long N=200;
	private final static long MOD=1_000_000_007l;
	
	private static class ValueCalculator	{
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
		public long calculateValue(long num,long den)	{
			long diff=10*den-num;
			if (diff<=0) return 0;
			long q=diff;
			while ((q%2)==0) q/=2;
			while ((q%5)==0) q/=5;
			if ((q<den)||(q<=num)) return 0;
			LongSet allDivs=DivisorHolder.getFromFirstPrimes(q,firstPrimes).getUnsortedListOfDivisors();
			long chosenQ=q;
			for (LongCursor cursor=allDivs.cursor();cursor.moveNext();)	{
				long n=cursor.elem();
				if ((n<chosenQ)&&(n>den)&&(n>=num)) chosenQ=n;
			}
			long result=0l;
			long n=den;
			int nDigits=0;
			long firstDigit=((10*n)/chosenQ);
			do	{
				long n10=n*10;
				long digit=n10/chosenQ;
				n=n10%chosenQ;
				result=(10*result+digit)%mod;
				++nDigits;
			}	while (n!=den);
			int numThrees=2+maxPower3(nDigits)-maxPower3(chosenQ)+maxPower3(den);
			int minNeededThrees=maxPower3(den);
			if ((firstDigit>=9)&&((minNeededThrees<=numThrees-2))) return (result*inv9)%mod;
			else if (((firstDigit==3)||(firstDigit==6)||(firstDigit==9))&&(minNeededThrees<=numThrees-1)) return (result*inv3)%mod;
			else return result;
		}
	}
	
	/*
	 * This returns the correct value, but it can be further optimised!
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ValueCalculator calcu=new ValueCalculator(N,MOD);
		long result=0l;
		for (long u=1;u<=N;++u)	{
			long num=u*u*u;
			for (long v=1;v<=N;++v) if (EulerUtils.areCoprime(u,v))	{
				long den=v*v*v;
				long x=calcu.calculateValue(num,den);
				System.out.println(String.format("%d/%d => %d.",num,den,x));
				result+=x;
				// result+=calcu.calculateValue(num,den);
			}
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
