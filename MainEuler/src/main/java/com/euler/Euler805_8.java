package com.euler;

import java.math.BigInteger;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler805_8 {
	private final static long N=400;
	
	private static class ValueCalculator	{
		private final long[] firstPrimes;
		public ValueCalculator(long n)	{
			firstPrimes=Primes.firstPrimeSieve(10l*n*n*n);
		}
		private static int maxPower3(long in)	{
			int result=0;
			while ((in%3)==0)	{
				++result;
				in/=3;
			}
			return result;
		}
		public BigInteger calculateValue(long num,long den)	{
			long diff=10*den-num;
			if (diff<=0) return BigInteger.ZERO;
			long q=diff;
			while ((q%2)==0) q/=2;
			while ((q%5)==0) q/=5;
			if ((q<den)||(q<=num)) return BigInteger.ZERO;
			LongSet allDivs=DivisorHolder.getFromFirstPrimes(q,firstPrimes).getUnsortedListOfDivisors();
			long chosenQ=q;
			for (LongCursor cursor=allDivs.cursor();cursor.moveNext();)	{
				long n=cursor.elem();
				if ((n<chosenQ)&&(n>den)&&(n>=num)) chosenQ=n;
			}
			BigInteger result=BigInteger.ZERO;
			long n=den;
			int nDigits=0;
			long firstDigit=((10*n)/chosenQ);
			do	{
				long n10=n*10;
				long digit=n10/chosenQ;
				n=n10%chosenQ;
				result=result.multiply(BigInteger.TEN).add(BigInteger.valueOf(digit));
				++nDigits;
			}	while (n!=den);
			int numThrees=2+maxPower3(nDigits)-maxPower3(chosenQ)+maxPower3(den);
			int minNeededThrees=maxPower3(den);
			if ((firstDigit>=9)&&((minNeededThrees<=numThrees-2)))	{
				BigInteger[] div=result.divideAndRemainder(BigInteger.valueOf(9));
				if (!div[1].equals(BigInteger.ZERO))	{
					throw new IllegalStateException("Catacrocker.");
				}
				return div[0];
			}	else if (((firstDigit==3)||(firstDigit==6)||(firstDigit==9))&&(minNeededThrees<=numThrees-1))	{
				BigInteger[] div=result.divideAndRemainder(BigInteger.valueOf(3));
				if (!div[1].equals(BigInteger.ZERO)) throw new IllegalStateException("Catacrocker.");
				return div[0];
			}
			return result;
		}
	}
	
	/*
	 * Ok, at least I've identified A PART OF the problem.
	 * 1/1 -> the result is 1, not 3.
	 * 1/10 -> the result is 10, not 90.
	 * For 1/7 I'm getting 3043478260869565217391 but I suspect the real result is 1014492753623188405797
	 */
	public static void main(String[] args)	{
		ValueCalculator calcu=new ValueCalculator(N);
		for (long num=1;num<=N;++num) for (long den=1;den<=N;++den) if (EulerUtils.areCoprime(num,den))	{
			BigInteger result=calcu.calculateValue(num,den);
			if (result.equals(BigInteger.ZERO)) continue;
			String resultStr=result.toString();
			String otherResultStr=resultStr.substring(1)+resultStr.charAt(0);
			BigInteger otherResult=new BigInteger(otherResultStr);
			BigInteger[] div1=result.divideAndRemainder(BigInteger.valueOf(den));
			if (!div1[1].equals(BigInteger.ZERO))	{
				throw new IllegalStateException("D:");
			}
			BigInteger[] div2=otherResult.divideAndRemainder(BigInteger.valueOf(num));
			if (!div2[1].equals(BigInteger.ZERO))	{
				throw new IllegalStateException("DD:");
			}
			if (!div1[0].equals(div2[0]))	{
				throw new IllegalStateException("DDD:");
			}
			{
				BigInteger[] cosi=result.divideAndRemainder(BigInteger.valueOf(3l));
				if (cosi[1].equals(BigInteger.ZERO))	{
					String resultStrCosi=cosi[0].toString();
					String otherResultCosi=resultStrCosi.substring(1)+resultStrCosi.charAt(0);
					BigInteger otherCosi=new BigInteger(otherResultCosi);
					BigInteger[] div1Cosi=cosi[0].divideAndRemainder(BigInteger.valueOf(den));
					BigInteger[] div2Cosi=otherCosi.divideAndRemainder(BigInteger.valueOf(num));
					if (div1Cosi[1].equals(BigInteger.ZERO)&&div2Cosi[1].equals(BigInteger.ZERO)&&div1Cosi[0].equals(div2Cosi[0]))	{
						throw new IllegalStateException("Ay mecachis. Pues podría haber dividido más.");
					}
				}
			}
			System.out.println(String.format("Resultado aparentemente correcto para %d/%d: %s.",num,den,result));
		}
	}
}
