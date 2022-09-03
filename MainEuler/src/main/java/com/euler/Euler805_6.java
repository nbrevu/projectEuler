package com.euler;

import java.math.BigInteger;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler805_6 {
	private final static long N=400;
	
	private static BigInteger getValue(long den,long q)	{
		BigInteger result=BigInteger.ZERO;
		long n=den;
		do	{
			long n10=n*10;
			long digit=n10/q;
			n=n10%q;
			result=result.multiply(BigInteger.TEN).add(BigInteger.valueOf(digit));
		}	while (n!=den);
		return result;
	}
	
	/*
	 * Ok, at least I've identified A PART OF the problem.
	 * 1/1 -> the result is 1, not 3.
	 * 1/10 -> the result is 10, not 90.
	 * For 1/7 I'm getting 3043478260869565217391 but I suspect the real result is 1014492753623188405797
	 */
	public static void main(String[] args)	{
		long[] firstPrimes=Primes.firstPrimeSieve(10*N);
		for (long num=1;num<=N;++num) for (long den=1;den<=N;++den) if (EulerUtils.areCoprime(num,den))	{
			long diff=10*den-num;
			if (diff<=0) continue;
			long q=diff;
			while ((q%2)==0) q/=2;
			while ((q%5)==0) q/=5;
			if ((q<den)||(q<=num)) continue;
			LongSet allDivs=DivisorHolder.getFromFirstPrimes(q,firstPrimes).getUnsortedListOfDivisors();
			long chosenQ=q;
			for (LongCursor cursor=allDivs.cursor();cursor.moveNext();)	{
				long n=cursor.elem();
				if ((n<chosenQ)&&(n>den)&&(n>=num)) chosenQ=n;
			}
			BigInteger result=getValue(den,chosenQ);
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
			System.out.println(String.format("Resultado aparentemente correcto para %d/%d: %s.",num,den,result));
		}
	}
}
