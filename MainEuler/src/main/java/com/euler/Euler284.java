package com.euler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Euler284 {
	// I REALLY, REALLY should do this in C++... that "14" is the perfect argument for a... TEMPLATE!!!!!
	private static class String14	{
		private final int[] digits;
		private final int[] squareDigits;
		public String14(int digit)	{
			digits=new int[1];
			digits[0]=digit;
			squareDigits=new int[2];
			int square=digit*digit;
			squareDigits[0]=square%14;
			squareDigits[1]=square/14;
		}
		public String14(String14 previous,int digit)	{
			int l=previous.digits.length;
			digits=new int[l+1];
			squareDigits=new int[l+l+2];
			for (int i=0;i<l;++i) digits[i]=previous.digits[i];
			digits[l]=digit;
			for (int i=0;i<l+l;++i) squareDigits[i]=previous.squareDigits[i];
			for (int i=0;i<l;++i) squareDigits[i+l]+=2*digit*previous.digits[i];
			squareDigits[l+l]+=digit*digit;
			for (int i=l;i<l+l+1;++i)	{
				int prev=squareDigits[i];
				squareDigits[i]=prev%14;
				squareDigits[i+1]+=prev/14;
			}
		}
		public boolean isSteadySquare()	{
			int l=digits.length-1;
			return squareDigits[l]==digits[l];
		}
		public boolean isAcceptable()	{
			return (digits[digits.length-1]>0)&&isSteadySquare();
		}
		public List<String14> nextLevel()	{
			List<String14> result=new ArrayList<>();
			for (int i=0;i<14;++i)	{
				String14 candidate=new String14(this,i);
				if (candidate.isSteadySquare()) result.add(candidate);
			}
			return result;
		}
		public static List<String14> getInitialValues()	{
			List<String14> result=new ArrayList<>();
			for (int i=2;i<14;++i)	{
				String14 candidate=new String14(i);
				if (candidate.isSteadySquare()) result.add(candidate);
			}
			return result;
		}
		public long getDigitsSum()	{
			long sum=0;
			for (int digit:digits) sum+=(long)digit;
			return sum;
		}
		@Override
		public String toString()	{
			StringBuilder sb=new StringBuilder();
			for (int i=digits.length-1;i>=0;--i) sb.append(CHARACTERS[digits[i]]);
			return sb.toString();
		}
		private static char[] CHARACTERS={'0','1','2','3','4','5','6','7','8','9','a','b','c','d'};
	}
	
	public static void main(String[] args)	{
		try (PrintStream out=new PrintStream(new File("D:\\out284.txt")))	{
			List<String14> fixedSizeStrings=String14.getInitialValues();
			out.println("1 => "+fixedSizeStrings);
			long sum=1;
			for (String14 candidate:fixedSizeStrings) if (candidate.isAcceptable()) sum+=candidate.getDigitsSum();
			for (int i=2;i<=10000;++i)	{
				List<String14> nextStrings=new ArrayList<>();
				for (String14 str:fixedSizeStrings) nextStrings.addAll(str.nextLevel());
				fixedSizeStrings=nextStrings;
				for (String14 candidate:fixedSizeStrings) if (candidate.isAcceptable()) sum+=candidate.getDigitsSum();
				out.println(""+i+" => "+fixedSizeStrings);
			}
			System.out.println(Long.toString(sum,14));
		}	catch (IOException exc)	{
			System.out.println("D'oh!");
			// Answer: 5a411d7b (letting it here for refactors...).
		}
	}
}
