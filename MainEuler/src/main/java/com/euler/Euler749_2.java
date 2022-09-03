package com.euler;

import java.util.OptionalLong;

import com.euler.common.CombinationIterator;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntLongMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntLongMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler749_2 {
	private final static int MAX_DIGITS=13;
	private final static int BASE=16;
	
	private static IntIntMap extractDigits(long in)	{
		IntIntMap result=HashIntIntMaps.newMutableMap();
		while (in>0)	{
			int digit=(int)(in%BASE);
			if (digit!=0) result.addValue(digit,1);
			in/=BASE;
		}
		return result;
	}
	
	private static IntIntMap extractDigits(int[] array)	{
		IntIntMap result=HashIntIntMaps.newMutableMap();
		for (int d:array) if (d!=0) result.addValue(d,1);
		return result;
	}
	
	private static class PowerCalculator	{
		private final IntObjMap<IntLongMap> powerCache;
		private static IntObjMap<IntLongMap> createPowerCache(long limit)	{
			IntObjMap<IntLongMap> result=HashIntObjMaps.newMutableMap();
			for (int i=2;i<BASE;++i)	{
				IntLongMap powers=HashIntLongMaps.newMutableMap();
				int exponent=1;
				long value=i;
				do	{
					powers.put(exponent,value);
					++exponent;
					value*=i;
				}	while (value<=limit);
				result.put(i,powers);
			}
			return result;
		}
		public PowerCalculator(int maxDigits)	{
			powerCache=createPowerCache(LongMath.pow(BASE,maxDigits));
		}
		private OptionalLong getPower(int digit,int exp)	{
			if (digit<=1) return OptionalLong.of(digit);
			IntLongMap powers=powerCache.get(digit);
			return powers.containsKey(exp)?OptionalLong.of(powers.get(exp)):OptionalLong.empty();
		}
		private OptionalLong getPowerSum(int[] digits,int exp)	{
			// Assumes that there is at least one digit greater than 1.
			long sum=0l;
			for (int d:digits)	{
				OptionalLong pow=getPower(d,exp);
				if (pow.isEmpty()) return pow;	// I.e. OptionalLong.empty();
				else sum+=pow.getAsLong();
			}
			return OptionalLong.of(sum);
		}
		public long sumValidCases(int[] digits)	{
			/*
			 * We are generating digit sets in increasing order, so if the last one is either one or zero, then the digits are all zeroes or ones.
			 * None of these cases count, so no need to check them.
			 */
			if (digits[digits.length-1]<=1) return 0;
			IntIntMap summary=extractDigits(digits);
			long result=0;
			for (int i=1;;++i)	{
				OptionalLong sum=getPowerSum(digits,i);
				if (sum.isEmpty()) return result;
				long n=sum.getAsLong();
				result+=sumIfValid(n-1,summary,i)+sumIfValid(n+1,summary,i);
			}
		}
		private long sumIfValid(long number,IntIntMap digitsToCompare,int exp)	{
			IntIntMap numDigits=extractDigits(number);
			if (numDigits.equals(digitsToCompare))	{
				System.out.println(String.format("%s (k=%d).",Long.toString(number,BASE),exp));
				return number;
			}	else return 0;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CombinationIterator iterator=new CombinationIterator(MAX_DIGITS,BASE,true,false);
		PowerCalculator calculator=new PowerCalculator(MAX_DIGITS);
		long result=0l;
		for (int[] digitSet:iterator) result+=calculator.sumValidCases(digitSet);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
