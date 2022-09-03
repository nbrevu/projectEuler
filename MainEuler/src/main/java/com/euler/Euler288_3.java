package com.euler;

import java.util.Iterator;

import com.google.common.math.LongMath;

public class Euler288_3 {
	// "Number 629527 appeared in position 1, but also in position 6308949. Therefore, there is a cycle of length 6308948."
	private final static long K1=290797;
	private final static long K2=50515093;
	private final static long PRIME=61;
	private final static int N=10000000;
	private final static int MOD_EXPONENT=10;
	
	private static class NumberGenerator implements Iterator<Long>	{
		private final long k2;
		public long n;
		public NumberGenerator(long k1,long k2)	{
			this.k2=k2;
			n=k1;
		}
		@Override
		public Long next()	{
			long result=n;
			n=(n*n)%k2;
			return result;
		}
		@Override
		public boolean hasNext()	{
			return true;
		}
	}
	
	private static class PrimeBaseRepresentation	{
		private final long representation[];
		private final long mod;
		
		public PrimeBaseRepresentation(long prime,int maxExpectedLength)	{
			representation=new long[maxExpectedLength];
			mod=prime;
		}
		
		public void addNumber(long number,int position)	{
			try	{
				int maxIndex=addNumberRecursive(number,position);
				fixMod(position,maxIndex);
			}	catch (IndexOutOfBoundsException oob)	{
				throw new RuntimeException("T'HAS PASAO, MACHO T'HAS PASAO, PUES TOMAS LO QUE QUIERAS QUE NO HAS ESTADO PESAO.");
			}
		}
		
		private int addNumberRecursive(long number,int position)	{
			if (number<mod)	{
				representation[position]+=number;
				return position;
			}
			long rem=number%mod;
			long quot=number/mod;
			representation[position]+=rem;
			return addNumberRecursive(quot,1+position);
		}
		
		private void fixMod(int minPosition,int maxPosition)	{
			long carry=0;
			int position=minPosition;
			while ((position<=maxPosition)||(carry>0))	{
				representation[position]+=carry;
				carry=representation[position]/mod;
				representation[position]%=mod;
				++position;
			}
		}
		
		public long getNumberInPosition(int pos)	{
			return representation[pos];
		}
		
		public int getMaxPower()	{
			for (int i=representation.length-1;;--i) if (representation[i]!=0) return i;
		}
	}
	
	private static PrimeBaseRepresentation getBigNumber(long prime,int iterations,long k1,long k2)	{
		int extraDigits=2+(int)Math.ceil(Math.log(k2)/Math.log(prime));
		PrimeBaseRepresentation result=new PrimeBaseRepresentation(prime,iterations+extraDigits);
		NumberGenerator gen=new NumberGenerator(k1,k2);
		for (int i=0;i<=iterations;++i) result.addNumber(gen.next()%prime,i);
		return result;
	}
	
	private static long[] getCoefficients(long prime,int maxExp)	{
		long factor=1;
		long[] result=new long[1+maxExp];
		result[0]=0;
		result[1]=1;
		for (int i=2;i<=maxExp;++i)	{
			factor*=prime;
			result[i]=factor+result[i-1];
		}
		return result;
	}
	
	private static long getAppearancesInFactorial(PrimeBaseRepresentation rep,long prime,int expMod)	{
		long result=0;
		long[] coefficients=getCoefficients(prime,expMod);
		long mod=LongMath.pow(prime,expMod);
		int maxPosition=rep.getMaxPower();
		for (int i=1;i<=maxPosition;++i)	{
			int coefPos=Math.min(i,expMod);
			long augend=coefficients[coefPos]*rep.getNumberInPosition(i);
			result=(result+augend)%mod;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		PrimeBaseRepresentation bigNumber=getBigNumber(PRIME,N,K1,K2);
		long result=getAppearancesInFactorial(bigNumber,PRIME,MOD_EXPONENT);
		System.out.println(result);
	}
}
