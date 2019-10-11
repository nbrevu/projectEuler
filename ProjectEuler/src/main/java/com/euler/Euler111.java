package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.euler.common.CombinationIterator;
import com.euler.common.Primes.RabinMiller;
import com.euler.common.Timing;
import com.euler.common.VariationIterator;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler111 {
	private final static int DIGITS=10;
	
	private final static List<Integer> WITNESSES=Arrays.asList(2,13,23,1662803);
	
	private static class CandidateGenerator	{
		private final static RabinMiller TESTER=new RabinMiller();
		private final int digit;
		private final int totalDigits;
		private final int otherDigits;
		private final int baseProduct;
		private final int[] placeholder;
		public CandidateGenerator(int digit,int repetitions,int totalDigits)	{
			this.digit=digit;
			this.totalDigits=totalDigits;
			otherDigits=totalDigits-repetitions;
			baseProduct=digit*repetitions;
			placeholder=new int[totalDigits];
		}
		private static int sum(int[] array)	{
			int result=0;
			for (int n:array) result+=n;
			return result;
		}
		private static boolean obviouslyNotPrime(int lastDigit)	{
			return (lastDigit%2==0)||(lastDigit%5==0);
		}
		private static long coalesce(int[] array)	{
			long result=0;
			for (int n:array)	{
				result*=10;
				result+=n;
			}
			return result;
		}
		private boolean anyForbiddenDigit(int[] array)	{
			for (int n:array) if (n==digit) return true;
			return false;
		}
		public LongSet getCandidates()	{
			LongSet result=HashLongSets.newMutableSet();
			for (int[] digits:new VariationIterator(otherDigits,10,false))	{
				if (((sum(digits)+baseProduct)%3==0)||anyForbiddenDigit(digits)) continue;
				for (int[] digitPositions:new CombinationIterator(otherDigits,totalDigits,false,false))	{
					int firstDigit=(digitPositions[0]==0)?digits[0]:digit;
					int lastDigit=(digitPositions[otherDigits-1]==totalDigits-1)?digits[otherDigits-1]:digit;
					if ((firstDigit==0)||obviouslyNotPrime(lastDigit)) continue;
					Arrays.fill(placeholder,digit);
					for (int i=0;i<otherDigits;++i) placeholder[digitPositions[i]]=digits[i];
					long candidate=coalesce(placeholder);
					if (TESTER.isPrime(BigInteger.valueOf(candidate),WITNESSES)) result.add(candidate);
				}
			}
			return result;
		}
	}
	
	private static long solve()	{
		long result=0;
		for (int i=0;i<=9;++i) for (int j=DIGITS-1;;--j)	{
			CandidateGenerator generator=new CandidateGenerator(i,j,DIGITS);
			LongSet candidates=generator.getCandidates();
			if (!candidates.isEmpty())	{
				for (LongCursor cursor=candidates.cursor();cursor.moveNext();) result+=cursor.elem();
				break;
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler111::solve);
	}
}
