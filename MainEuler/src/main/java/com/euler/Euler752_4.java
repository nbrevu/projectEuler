package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.LongMatrix;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;

public class Euler752_4 {
	private final static int LIMIT=IntMath.pow(10,6);
	
	private static class CycleCalculator	{
		private final static LongMatrix M=createMatrix();
		private static LongMatrix createMatrix()	{
			LongMatrix result=new LongMatrix(2);
			result.assign(0,0,1);
			result.assign(0,1,7);
			result.assign(1,0,1);
			result.assign(1,1,1);
			return result;
		}
		private static long pow(long base,int exp)	{
			long result=base;
			for (int i=2;i<=exp;++i) result*=base;
			return result;
		}
		private static boolean isEye(LongMatrix m)	{
			return ((m.get(0,0)==1l)&&(m.get(0,1)==0l)&&(m.get(1,0)==0l)&&(m.get(1,1)==1l));
		}
		private final long[] firstPrimes;
		private final long[] cache;
		public CycleCalculator(int limit)	{
			firstPrimes=Primes.firstPrimeSieve((long)limit);
			cache=new long[limit];
		}
		public long calculate(int value)	{
			long result;
			if (firstPrimes[value]==0) result=calculateAsPrime(value);
			else	{
				DivisorHolder decomp=DivisorHolder.getFromFirstPrimes(value,firstPrimes);
				LongIntMap divMap=decomp.getFactorMap();
				if (divMap.size()==1) result=calculateAsPrimePower(value,divMap);
				else result=calculateAsCombination(value,divMap);
			}
			cache[value]=result;
			return result;
		}
		private long calculateAsPrime(int value)	{
			if (value==7) return 7l;	// Special case.
			DivisorHolder div1=DivisorHolder.getFromFirstPrimes(value-1,firstPrimes);
			DivisorHolder div2=DivisorHolder.getFromFirstPrimes(value+1,firstPrimes);
			DivisorHolder allDivs=DivisorHolder.combine(div1,div2);
			long[] divisors=allDivs.getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(divisors);
			// There are more efficient ways to do this (caching temporary powers), but this is a good start.
			LongMatrix m=M;
			for (int i=1;i<divisors.length;++i)	{
				m=m.multiply(M.pow(divisors[i]-divisors[i-1],value),value);
				if (isEye(m)) return divisors[i];
			}
			throw new IllegalStateException("Cycle not found.");
		}
		private long calculateAsPrimePower(int value,LongIntMap divMap)	{
			LongCursor cursor=divMap.keySet().cursor();
			cursor.moveNext();
			long prime=cursor.elem();
			return cache[value/(int)prime]*prime;
		}
		private long calculateAsCombination(int value,LongIntMap divMap)	{
			LongIntCursor cursor=divMap.cursor();
			cursor.moveNext();
			// Maybe a cache instead of this "pow" call used so many times?
			long result=cache[(int)pow(cursor.key(),cursor.value())];
			while (cursor.moveNext())	{
				long tmpResult=cache[(int)pow(cursor.key(),cursor.value())];
				result=EulerUtils.lcm(result,tmpResult);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CycleCalculator calculator=new CycleCalculator(LIMIT);
		boolean add4=false;
		long result=0l;
		for (int i=5;i<LIMIT;i+=(add4?4:2),add4=!add4) result+=calculator.calculate(i);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
