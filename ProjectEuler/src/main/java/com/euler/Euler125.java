package com.euler;

import java.math.RoundingMode;

import com.euler.common.Timing;
import com.google.common.math.IntMath;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler125 {
	private final static int MAX_DIGITS=8;
	private final static int LIMIT=IntMath.pow(10,MAX_DIGITS);
	
	private static class PalindromeChecker	{
		private final int[] digits;
		public PalindromeChecker(int maxSize)	{
			digits=new int[maxSize];
		}
		public boolean isPalindrome(int in)	{
			int index=0;
			while (in>0)	{
				digits[index]=in%10;
				in/=10;
				++index;
			}
			for (int i=0;i<index/2;++i) if (digits[i]!=digits[index-1-i]) return false;
			return true;
		}
	}
	
	private static long solve()	{
		int sq=IntMath.sqrt(LIMIT/2,RoundingMode.UP);
		long[] squareSums=new long[1+sq];
		for (int i=1;i<=sq;++i) squareSums[i]=squareSums[i-1]+i*i;
		IntSet palindromeSqDiffs=HashIntSets.newMutableSet();
		PalindromeChecker checker=new PalindromeChecker(MAX_DIGITS);
		for (int i=0;i<sq;++i) for (int j=i+2;j<=sq;++j)	{
			int diff=(int)(squareSums[j]-squareSums[i]);
			if (diff>=LIMIT) break;
			else if (checker.isPalindrome(diff)) palindromeSqDiffs.add(diff);
		}
		long result=0l;
		for (IntCursor cursor=palindromeSqDiffs.cursor();cursor.moveNext();) result+=cursor.elem();
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler125::solve);
	}
}
