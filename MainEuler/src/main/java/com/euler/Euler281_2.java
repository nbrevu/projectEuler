package com.euler;

import static com.euler.common.BigIntegerUtils.pow;

import java.math.BigInteger;

import com.euler.common.BigIntegerUtils.BigFactorialCache;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler281_2 {
	private static class TheoreticalCalculator	{
		private final BigFactorialCache cache;
		private final boolean[] composites;
		public TheoreticalCalculator()	{
			cache=new BigFactorialCache(10);
			composites=Primes.sieve(100);
		}
		public BigInteger get(int m,int n)	{
			if (n==1) return cache.get(m-1);
			if (n==4)	{
				BigInteger term1=cache.get(m*4).divide(pow(cache.get(4),m));
				BigInteger term2=cache.get(m*2).divide(pow(cache.get(2),m));
				BigInteger num1=term1.subtract(term2);
				BigInteger[] divResult=num1.divideAndRemainder(BigInteger.valueOf(m*4));
				if (!divResult[1].equals(BigInteger.ZERO)) throw new RuntimeException("Scheiße!!!!! Parece que "+num1+" no es múltiplo de "+(m*4)+".");
				BigInteger result=divResult[0];
				BigInteger num2=term2.subtract(cache.get(m));
				divResult=num2.divideAndRemainder(BigInteger.valueOf(m*2));
				if (!divResult[1].equals(BigInteger.ZERO)) throw new RuntimeException("Zweite Scheiße!!!!! Parece que "+num2+" no es múltiplo de "+(m*2)+".");
				result=result.add(divResult[0]);
				return result.add(cache.get(m-1));
			}
			if (composites[n]) throw new RuntimeException("AÚN NO POPOCH.");
			int prod=m*n;
			return cache.get(prod).divide(pow(cache.get(n),m)).subtract(cache.get(m)).divide(BigInteger.valueOf(prod)).add(cache.get(m-1));
		}
	}
	
	public static void main(String[] args)	{
		TheoreticalCalculator calc=new TheoreticalCalculator();
		BigInteger limit=BigInteger.valueOf(LongMath.pow(10l,15));
		for (int n=1;;++n) try	{
			int m=2;
			for (;;++m)	{
				BigInteger result=calc.get(m,n);
				System.out.println("f("+m+","+n+")="+result+".");
				if (result.compareTo(limit)>0) break;
			}
			if (m==2) break;
		}	catch (RuntimeException exc)	{
			if (n==4) System.out.println(exc.getMessage());
		}
	}
}
