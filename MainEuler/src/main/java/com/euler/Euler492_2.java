package com.euler;

import java.math.BigInteger;

public class Euler492_2 {
	/*
	private final static long PRIME_START=LongMath.pow(10,9);
	private final static long PRIME_SHIFT=LongMath.pow(10,7);
	private final static long PRIME_END=PRIME_START+PRIME_SHIFT;
	private final static long GOAL=LongMath.pow(10l,15);
	*/
	
	/*
	 * A(n)=(238+22*sqrt(117))^(2^n)
	 * B(n)=(238-22*sqrt(117))^(2^n)
	 * Podemos calcular potencias en el espacio de enteros Z[sqrt(117)] (mod P), con un vector de dos elementos y con matrices 2x2.
	 * Sigue siendo lineal, PERO: hay p^2-1 elementos no nulos. El "totient" (en 2D) es probablemente p^2-1. Es posible que podamos reducir el
	 * exponente a (2^n%(p^2-1)).
	 * 
	 * Primer paso: comprobar que la fórmula básica basada en A(n) y B(n) da valores correctos para primos pequeños.
	 * ES FUNKTIONIERT!
	 */
	
	private static class Number117	{
		public final static BigInteger RADICAND=BigInteger.valueOf(117);
		public final BigInteger integerPart;
		public final BigInteger sqrtPart;
		public Number117(BigInteger integerPart,BigInteger sqrtPart)	{
			this.integerPart=integerPart;
			this.sqrtPart=sqrtPart;
		}
		public Number117 times(Number117 other)	{
			BigInteger newIntegerPart=integerPart.multiply(other.integerPart).add(RADICAND.multiply(sqrtPart).multiply(other.sqrtPart));
			BigInteger newSqrtPart=integerPart.multiply(other.sqrtPart).add(sqrtPart.multiply(other.integerPart));
			return new Number117(newIntegerPart,newSqrtPart);
		}
	}
	
	private final static BigInteger FIRST_COEF=BigInteger.valueOf(11);
	private final static BigInteger SECOND_COEF=BigInteger.ONE;
	private final static BigInteger SECOND_COEF_NEG=SECOND_COEF.negate();
	private final static Number117 A2=new Number117(FIRST_COEF,SECOND_COEF);
	private final static Number117 B2=new Number117(FIRST_COEF,SECOND_COEF_NEG);
	private final static BigInteger FIVE=BigInteger.valueOf(5);
	private final static BigInteger SIX=BigInteger.valueOf(6);
	
	private static BigInteger fineCalculation(int index)	{
		int n=index-1;
		Number117 a2exp=A2;
		Number117 b2exp=B2;
		BigInteger pow2=BigInteger.TWO;
		for (int i=0;i<n;++i)	{
			a2exp=a2exp.times(a2exp);
			b2exp=b2exp.times(b2exp);
			pow2=pow2.multiply(pow2);
		}
		if (a2exp.sqrtPart.add(b2exp.sqrtPart).signum()!=0) throw new RuntimeException("Lo que me habéis endiñao pa papear me roe las tripas.");
		BigInteger num=a2exp.integerPart.add(b2exp.integerPart);
		BigInteger[] division=num.divideAndRemainder(pow2);
		if (division[1].signum()!=0) throw new RuntimeException("Gobakken sidonna.");
		BigInteger lastNum=division[0].subtract(FIVE);
		division=lastNum.divideAndRemainder(SIX);
		if (division[1].signum()!=0) throw new RuntimeException("Elegí un mal día para dejar de esnifar pegamento.");
		return division[0];
	}
	
	public static void main(String[] args)	{
		BigInteger a=BigInteger.ONE;
		BigInteger three=BigInteger.valueOf(3);
		for (int i=2;i<20;++i)	{
			a=(a.multiply(SIX).add(BigInteger.TEN)).multiply(a).add(three);
			BigInteger b=fineCalculation(i);
			System.out.println(a+"<==>"+b+"...");
			if (!a.equals(b)) throw new RuntimeException("Es gefällt mir überhaupt nicht. ÜBERHAUPT NICHT!");
		}
		/*
		int totalPrimeLimit=(int)(PRIME_START+PRIME_SHIFT);
		boolean[] composites=Primes.sieve(totalPrimeLimit);
		*/
	}
}
