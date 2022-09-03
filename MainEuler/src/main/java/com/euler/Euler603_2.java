package com.euler;

import com.euler.common.LongMatrix;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler603_2 {
	private final static int PRIMES=IntMath.pow(10,6);
	private final static long REPETITIONS=LongMath.pow(10l,12);
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private final static int PRIME_LIMIT=20*PRIMES;
	
	/*
	 * El estado se compone de tres variables: (x0,x1,x2).
	 * x0 = suma total hasta ahora.
	 * x1 = suma de todos los números que pueden ser concatenados.
	 * x2 = cantidad total de números que pueden ser encadenados.
	 * 
	 * Evolución al añadir un dígito más, D:
	 * x2(n+1) = x2(n)+1.
	 * x1(n+1) = 10*x1(n) + D*x2(n+1) = 10*x1(n) + D*x2(n) + D.
	 * x0(n+1) = x0(n) + x1(n+1) = x0(n) + 10*x1(n) + D*x2(n) + D.
	 * 
	 * Podemos simular las constantes añadiendo una variable falsa, x3(n) = 1.
	 */
	private static class IncreasingDigitSeparator	{
		private long[] digits;
		private long nextPower;
		public IncreasingDigitSeparator()	{
			digits=new long[1];
			nextPower=10l;
		}
		public long[] separate(long number)	{
			if (number>nextPower)	{
				nextPower*=10l;
				digits=new long[1+digits.length];
			}
			for (int i=digits.length-1;i>=0;--i)	{
				digits[i]=number%10l;
				number/=10l;
			}
			return digits;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] primes=Primes.listLongPrimesAsArray(PRIME_LIMIT);
		IncreasingDigitSeparator separator=new IncreasingDigitSeparator();
		LongMatrix toMultiply=new LongMatrix(4);
		toMultiply.assign(0,0,1l);
		toMultiply.assign(0,1,10l);
		toMultiply.assign(1,1,10l);
		toMultiply.assign(2,2,1l);
		toMultiply.assign(2,3,1l);
		toMultiply.assign(3,3,1l);
		LongMatrix matrix=new LongMatrix(4);
		for (int i=0;i<4;++i) matrix.assign(i,i,1l);
		for (int i=0;i<PRIMES;++i)	{
			for (long l:separator.separate(primes[i]))	{
				toMultiply.assign(0,2,l);
				toMultiply.assign(0,3,l);
				toMultiply.assign(1,2,l);
				toMultiply.assign(1,3,l);
				matrix=toMultiply.multiply(matrix,MOD);
			}
		}
		matrix=matrix.pow(REPETITIONS,MOD);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(matrix.get(0,3));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
