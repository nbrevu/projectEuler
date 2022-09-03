package com.euler;

import java.math.BigInteger;

public class Euler612 {
	// Esto es un poco más complicado de lo que pensaba, pero aún así creo que estoy cerca.
	private final static int DIGIT_LIMIT=2;
	private final static long MOD=1000267129l;
	private final static int BASE=10;
	
	private static class CombinationCounter	{
		private final static BigInteger TWO=BigInteger.valueOf(2);
		
		private final int digitLimit;
		private final int base;
		// For this problem, long[] is enough. For the general case, BigInteger[] is needed.
		private final BigInteger[] numberCountWithoutZero;
		private final BigInteger[] numberCountWithZero;
		public CombinationCounter(int digitLimit,int base)	{
			this.digitLimit=digitLimit;
			this.base=base;
			numberCountWithoutZero=getCountWithoutZero(digitLimit,base);
			numberCountWithZero=getCountWithZero(digitLimit,base);
		}
		
		private static class NumberCounter	{
			private final int maxDigits;
			private final BigInteger[][] counters;
			public NumberCounter(int base,int maxDigits)	{
				this.maxDigits=maxDigits;
				counters=calculateCounters(base,maxDigits);
			}
			private BigInteger getNumber(int digits,int code)	{
				return counters[digits-1][code];
			}
			private BigInteger getNumber(int code)	{
				BigInteger result=BigInteger.ZERO;
				for (int i=0;i<maxDigits;++i) result=result.add(counters[i][code]);
				return result;
			}
			private static BigInteger[][] calculateCounters(int base,int maxDigits)	{
				int cases=1<<base;
				BigInteger[][] result=new BigInteger[maxDigits][cases];
				for (int i=0;i<maxDigits;++i) for (int j=0;j<maxDigits;++j) result[i][j]=BigInteger.ZERO;
				int[] digitIndices=new int[base];
				digitIndices[0]=1;
				for (int i=1;i<base;++i) digitIndices[i]=digitIndices[i-1]<<1;
				// Initial row. Note that 0 is excluded.
				for (int i=1;i<base;++i) result[0][digitIndices[i]]=BigInteger.ONE;
				// Remaining rows.
				for (int j=1;j<maxDigits;++j)	{
					for (int i=0;i<cases;++i)	{
						BigInteger initial=result[j-1][i];
						if (BigInteger.ZERO.equals(initial)) continue;
						for (int k=0;k<base;++k)	{
							int newIndex=i|digitIndices[k];
							result[j][newIndex]=result[j][newIndex].add(initial);
						}
					}
				}
				return result;
			}
		}
		
		// DAS GRÖßTE REVELACIÓN! Este método, y el siguiente, se pueden hacer a la vez 
		// gracias a nuestra gran amiga la programación dinámica.
		// A ver si en cuanto termine la PEC de Mecánica Teórica lo hago, oder?
		private static BigInteger[] getCountWithoutZero(int digitLimit,int base)	{
			BigInteger[] result=new BigInteger[1+base];
			for (int i=1;i<=base;++i) result[i]=BigInteger.ZERO;
			// TEHDÄ!!!!!
			return result;
		}
		private static BigInteger[] getCountWithZero(int digitLimit,int base)	{
			BigInteger[] result=new BigInteger[1+base];
			for (int i=1;i<=base;++i) result[i]=BigInteger.ZERO;
			// TEHDÄ!!!!!
			return result;
		}
		public BigInteger getAllCombinations()	{
			BigInteger result=BigInteger.ZERO;
			int nMax=1<<base;	// 2^base.
			for (int i=1;i<nMax;++i)	{
				int bitCount=Integer.bitCount(i);
				BigInteger base=(((i%2)==1)?numberCountWithZero:numberCountWithoutZero)[bitCount];
				BigInteger augend=getTriangular(base);
				if ((bitCount%2)==1) result=result.add(augend);
				else result=result.subtract(augend);
			}
			return result;
		}
		private static BigInteger getTriangular(BigInteger in)	{
			return in.multiply(in.subtract(BigInteger.ONE)).divide(TWO);
		}
	}
	
	public static void main(String[] args)	{
		CombinationCounter counter=new CombinationCounter(DIGIT_LIMIT,BASE);
		BigInteger result=counter.getAllCombinations();
		System.out.println(result.toString());
		BigInteger modded=result.mod(BigInteger.valueOf(MOD));
		System.out.println(modded.toString());
	}
}
