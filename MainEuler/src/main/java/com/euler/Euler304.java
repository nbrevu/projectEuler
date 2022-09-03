package com.euler;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;
import java.util.List;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler304 {
	// Not at the first attempt, but only because of a very silly error. Otherwise, good :).
	private final static long STARTING_POINT=LongMath.pow(10l,14);
	private final static long NEEDED_NUMBERS=LongMath.pow(10l,5);
	private final static List<Long> PRIMES=Primes.listLongPrimes(11*LongMath.pow(10l,6));
	private final static long MOD=1234567891011l;
	
	private static boolean[] displacedSieve(long startingPoint,int size)	{
		boolean[] composites=new boolean[1+size];
		for (long p:PRIMES)	{
			long mod=startingPoint%p;
			int first=(mod==0)?0:((int)(p-mod));
			for (int i=first;i<size;i+=(int)p) composites[i]=true;
		}
		return composites;
	}
	
	public static long[] getFirstPrimesAfterPoint(long startingPoint,int needed)	{
		long[] result=new long[needed];
		boolean[] composites=displacedSieve(startingPoint,needed*100);
		int index=0;
		for (int i=1;i<composites.length;i+=2) if (!composites[i])	{
			result[index]=startingPoint+i;
			++index;
			if (index==needed) return result;
		}
		throw new IllegalStateException("Not enough size for the sieve. Only "+index+" primes found.");
	}
	
	private static class Matrix	{
		private final BigInteger[][] coeffs;
		private Matrix(BigInteger a00,BigInteger a01,BigInteger a10,BigInteger a11)	{
			coeffs=new BigInteger[2][2];
			coeffs[0][0]=a00;
			coeffs[0][1]=a01;
			coeffs[1][0]=a10;
			coeffs[1][1]=a11;
		}
		public static Matrix IDENTITY=new Matrix(ONE,ZERO,ZERO,ONE);
		public static Matrix FIBO_BASE=new Matrix(ONE,ONE,ONE,ZERO);
		public Matrix multiply(Matrix other,BigInteger mod)	{
			BigInteger a00=this.coeffs[0][0];
			BigInteger a01=this.coeffs[0][1];
			BigInteger a10=this.coeffs[1][0];
			BigInteger a11=this.coeffs[1][1];
			BigInteger b00=other.coeffs[0][0];
			BigInteger b01=other.coeffs[0][1];
			BigInteger b10=other.coeffs[1][0];
			BigInteger b11=other.coeffs[1][1];
			BigInteger c00=(a00.multiply(b00)).add(a01.multiply(b10)).mod(mod);
			BigInteger c01=(a00.multiply(b01)).add(a01.multiply(b11)).mod(mod);
			BigInteger c10=(a10.multiply(b00)).add(a11.multiply(b10)).mod(mod);
			BigInteger c11=(a10.multiply(b01)).add(a11.multiply(b11)).mod(mod);
			return new Matrix(c00,c01,c10,c11);
		}
		public long getHead()	{
			return coeffs[0][0].longValue();
		}
		@Override
		public String toString()	{
			return "[["+coeffs[0][0]+","+coeffs[0][1]+"],["+coeffs[1][0]+","+coeffs[1][1]+"]]";
		}
	}
	
	private static Matrix getPower(Matrix in,long power,BigInteger mod)	{
		Matrix result=Matrix.IDENTITY;
		Matrix tmpProduct=in;
		while (power>0)	{
			if ((power%2)==1l) result=result.multiply(tmpProduct,mod);
			tmpProduct=tmpProduct.multiply(tmpProduct,mod);
			power/=2;
		}
		return result;
	}
	
	private static Matrix[] getPowerCache(Matrix in,int size,BigInteger mod)	{
		Matrix[] result=new Matrix[1+size];
		result[0]=Matrix.IDENTITY;
		result[1]=in;
		for (int i=2;i<=size;++i) result[i]=result[i-1].multiply(in,mod);
		return result;
	}
	
	public static void main(String[] args)	{
		BigInteger mod=BigInteger.valueOf(MOD);
		long[] primes=getFirstPrimesAfterPoint(STARTING_POINT,(int)NEEDED_NUMBERS);
		Matrix[] cache=getPowerCache(Matrix.FIBO_BASE,500,mod);
		long sum=0l;
		long previous=STARTING_POINT;
		Matrix previousMatrix=getPower(Matrix.FIBO_BASE,STARTING_POINT-1,mod);
		for (long prime:primes)	{
			int diff=(int)(prime-previous);
			Matrix newMatrix=previousMatrix.multiply(cache[diff],mod);
			sum=(sum+newMatrix.getHead())%MOD;
			previous=prime;
			previousMatrix=newMatrix;
		}
		System.out.println(sum);
	}
}
