package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler492_4 {
	private final static int PRIME_START=IntMath.pow(10,9);
	private final static int PRIME_SHIFT=IntMath.pow(10,7);
	private final static int PRIME_END=PRIME_START+PRIME_SHIFT;
	private final static long GOAL=LongMath.pow(10l,15);
	
	private static class Number13	{
		public final long integerPart;
		public final long sqrtPart;
		public final static Number13 ONE=new Number13(1,0);
		public Number13(long integerPart,long sqrtPart)	{
			this.integerPart=integerPart;
			this.sqrtPart=sqrtPart;
		}
		public Number13 timesMod(Number13 other,long mod)	{
			long partWithFactor=(sqrtPart*other.sqrtPart)%mod;
			long newIntegerPart=((integerPart*other.integerPart)+(13*partWithFactor))%mod;
			long newSqrtPart=((integerPart*other.sqrtPart)+(sqrtPart*other.integerPart))%mod;
			return new Number13(newIntegerPart,newSqrtPart);
		}
		public Number13 exp(long pow,long mod)	{
			Number13 result=ONE;
			Number13 factor=this;
			while (pow>0)	{
				if ((pow%2)==1) result=result.timesMod(factor,mod);
				factor=factor.timesMod(factor,mod);
				pow/=2;
			}
			return result;
		}
	}
	
	private static long calculateModPrimeWithFinesse(long prime,long index)	{
		long n=index-1;
		/*
		 * Cycle length of the Z[sqrt(N)] field modulo prime=prime^2-1. We need BigIntegers because the number is expected to be around 2^60,
		 * and the standard "expMod" for longs only works for values<=2^31.5. Still, after the modulus we expect to have a value that fits in a
		 * long, so the rest of the calculations use 64-bit integers and not big ones.
		 */
		BigInteger totient2=BigInteger.valueOf(prime*prime-1);
		BigInteger bigExp=EulerUtils.expMod(BigInteger.TWO,BigInteger.valueOf(n),totient2);
		long exp=bigExp.longValueExact();
		Number13 a=new Number13(11,3);
		Number13 b=new Number13(11,prime-3);
		Number13 aExp=a.exp(exp,prime);
		Number13 bExp=b.exp(exp,prime);
		if ((aExp.sqrtPart+bExp.sqrtPart)%prime!=0) throw new RuntimeException("Lo que me habéis endiñao pa papear me roe las tripas.");
		long num=aExp.integerPart+bExp.integerPart;
		// 2^(-1) mod prime=(prime+1)/2, which is convenient.
		long denInverse=EulerUtils.expMod((prime+1)/2,exp,prime);
		long bn=(num*denInverse)%prime;
		long result=(bn+prime-5)*EulerUtils.modulusInverse(6,prime);
		return result%prime;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		boolean[] composites=Primes.sieve(PRIME_END);
		long result=0;
		for (int i=PRIME_START;i<=PRIME_END;++i) if (!composites[i]) result+=calculateModPrimeWithFinesse(i,GOAL);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
