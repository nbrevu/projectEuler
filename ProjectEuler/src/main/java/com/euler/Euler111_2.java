package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.CombinationIterator;
import com.euler.common.Primes.RabinMiller;
import com.euler.common.Timing;
import com.euler.common.VariationIterator;

public class Euler111_2 {
	private final static int DIGITS=24;
	
	private final static List<Integer> WITNESSES=Arrays.asList(2,3,5,7,11,13,17,19,23,29,31,37,41);
	
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
		private static BigInteger coalesce(int[] array)	{
			BigInteger result=BigInteger.ZERO;
			for (int n:array) result=result.multiply(BigInteger.TEN).add(BigInteger.valueOf(n));
			return result;
		}
		private boolean anyForbiddenDigit(int[] array)	{
			for (int n:array) if (n==digit) return true;
			return false;
		}
		public Set<BigInteger> getCandidates()	{
			Set<BigInteger> result=new HashSet<>();
			for (int[] digits:new VariationIterator(otherDigits,10,false))	{
				if (((sum(digits)+baseProduct)%3==0)||anyForbiddenDigit(digits)) continue;
				for (int[] digitPositions:new CombinationIterator(otherDigits,totalDigits,false,false))	{
					int firstDigit=(digitPositions[0]==0)?digits[0]:digit;
					int lastDigit=(digitPositions[otherDigits-1]==totalDigits-1)?digits[otherDigits-1]:digit;
					if ((firstDigit==0)||obviouslyNotPrime(lastDigit)) continue;
					Arrays.fill(placeholder,digit);
					for (int i=0;i<otherDigits;++i) placeholder[digitPositions[i]]=digits[i];
					BigInteger candidate=coalesce(placeholder);
					if (TESTER.isPrime(candidate,WITNESSES)) result.add(candidate);
				}
			}
			return result;
		}
	}
	
	private static BigInteger sum(Set<BigInteger> set)	{
		return set.stream().reduce(BigInteger.ZERO,BigInteger::add);
	}
	
	private static String solve()	{
		BigInteger result=BigInteger.ZERO;
		for (int i=0;i<=9;++i) for (int j=DIGITS-1;;--j)	{
			CandidateGenerator generator=new CandidateGenerator(i,j,DIGITS);
			Set<BigInteger> candidates=generator.getCandidates();
			if (!candidates.isEmpty())	{
				System.out.println("M("+DIGITS+","+i+")="+j+".");
				System.out.println("N("+DIGITS+","+i+")="+candidates.size()+".");
				System.out.println("S("+DIGITS+","+i+")="+sum(candidates)+".");
				result=candidates.stream().reduce(result,BigInteger::add);
				break;
			}
		}
		return result.toString();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler111_2::solve);
	}
}
