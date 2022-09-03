package com.euler;

import java.math.BigInteger;

public class Euler612_2 {
	private final static int DIGIT_LIMIT=40;
	// private final static long MOD=1000267129l;
	private final static int BASE=12;
	
	private final static BigInteger TWO=BigInteger.valueOf(2);

	private static class NumberCounter	{
		private final int maxDigits;
		public final int cases;
		private final BigInteger[][] counters;
		public NumberCounter(int base,int maxDigits)	{
			this.maxDigits=maxDigits;
			cases=1<<base;
			counters=calculateCounters(base,maxDigits);
		}
		private BigInteger getCounter(int code)	{
			BigInteger result=BigInteger.ZERO;
			for (int i=0;i<maxDigits;++i) result=result.add(counters[i][code]);
			return result;
		}
		private BigInteger getCounterExtended(int code)	{
			BigInteger result=BigInteger.ZERO;
			for (int i=code;i<cases;++i) if ((code&i)==code) result=result.add(getCounter(i));
			return result;
		}
		private BigInteger[][] calculateCounters(int base,int maxDigits)	{
			BigInteger[][] result=new BigInteger[maxDigits][cases];
			for (int i=0;i<maxDigits;++i) for (int j=0;j<cases;++j) result[i][j]=BigInteger.ZERO;
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

	private static BigInteger getTriangular(BigInteger in)	{
		return in.multiply(in.subtract(BigInteger.ONE)).divide(TWO);
	}

	public static void main(String[] args)	{
		NumberCounter counter=new NumberCounter(BASE,DIGIT_LIMIT);
		BigInteger result=BigInteger.ZERO;
		for (int i=1;i<counter.cases;++i)	{
			BigInteger augend=getTriangular(counter.getCounterExtended(i));
			if ((Integer.bitCount(i)%2)==1) result=result.add(augend);
			else result=result.subtract(augend);
		}
		// result=result.mod(BigInteger.valueOf(MOD));
		System.out.println(result.toString());
	}
}
