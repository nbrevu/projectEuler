package com.euler;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import java.math.BigInteger;
import java.util.TreeSet;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler399_2 {
	private final static int LOOKING_FOR=IntMath.pow(10,8);
	private final static int MAX_DIGITS=16;
	private final static long MOD=LongMath.pow(10l,MAX_DIGITS);
	
	private final static int LIMIT=5*IntMath.pow(10,8);
	private final static int PRIME_LIMIT=2*IntMath.pow(10,6);
	
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
	
	private static long getNthFibonacci(long index,int mod)	{
		Matrix mat=getPower(Matrix.FIBO_BASE,index-1,BigInteger.valueOf(mod));
		return mat.getHead();
	}
	
	private static int getBaseCandidate(int prime,int[] firstPrimes)	{
		if (prime==5) return prime;
		int rem=prime%5;
		int maxMultiple=((rem==1)||(rem==4))?(prime-1):(prime+1);
		DivisorHolder factors=DivisorHolder.getFromFirstPrimes(maxMultiple,firstPrimes);
		// There is repetition here, so this can be sped up. It takes patience to get it right, though.
		for (long d:new TreeSet<>(factors.getUnsortedListOfDivisors())) if (getNthFibonacci(d,prime)==0l) return (int)d;
		throw new RuntimeException("The assumptions have failed!");
	}
	
	private static long getFiboLastDigits(int index)	{
		long prevF=1l;
		long curF=1l;
		for (int i=2;i<index;++i)	{
			long nextF=(prevF+curF)%MOD;
			prevF=curF;
			curF=nextF;
		}
		return curF;
	}
	
	private static String getSciFibo(int index)	{
		long prevF=1l;
		long curF=1l;
		int movedDigits=MAX_DIGITS-1;
		for (int i=2;i<index;++i)	{
			long nextF=prevF+curF;
			if (nextF>MOD)	{
				++movedDigits;
				prevF=curF/10l;
				curF=nextF/10;
			}	else	{
				prevF=curF;
				curF=nextF;
			}
		}
		curF/=LongMath.pow(10,MAX_DIGITS-2);
		return (curF/10)+"."+(curF%10)+"e"+movedDigits;
	}
	
	public static void main(String[] args)	{
		/*
		 * It turns out that the fib index is 130775524, and the biggest prime needed is 1429913, which is a divisor of F(67).
		 */
		long tic=System.nanoTime();
		int[] firstPrimes=Primes.firstPrimeSieve(PRIME_LIMIT);
		boolean[] sieved=new boolean[LIMIT];
		for (int p=2;p<PRIME_LIMIT;++p) if (firstPrimes[p]==0)	{
			int firstFiboMultipleOfPrime=getBaseCandidate(p,firstPrimes);
			long product=((long)p)*((long)firstFiboMultipleOfPrime);
			if (product>=LIMIT) continue;
			int toSieve=(int)product;
			for (int i=toSieve;i<LIMIT;i+=toSieve) sieved[i]=true;
		}
		int count=0;
		int foundIndex=-1;
		for (int i=1;;++i) if (!sieved[i])	{
			++count;
			if (count==LOOKING_FOR)	{
				foundIndex=i;
				break;
			}	else if (i==sieved.length-1) throw new RuntimeException("Sólo he encontrado "+count+" casos.");
		}
		String result=getFiboLastDigits(foundIndex)+","+getSciFibo(foundIndex);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
