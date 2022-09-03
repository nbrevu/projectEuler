package com.euler;

import com.google.common.math.LongMath;

public class Euler486 {
	private static boolean[] BOOLEANS=new boolean[] {false,true};
	
	private static boolean isPalindrome(boolean[] string,int start,int end)	{
		while (start<end)	{
			if (string[start]!=string[end]) return false;
			++start;
			--end;
		}
		return true;
	}
	
	private static long countValidStrings(int size)	{
		boolean[] string=new boolean[size];
		return countValidStrings(string,0);
	}
	
	private static long countValidStrings(boolean[] string,int current)	{
		if (current>=string.length) return 0l;
		long result=0l;
		for (boolean b:BOOLEANS)	{
			string[current]=b;
			boolean foundPalindrome=false;
			for (int i=0;i<=current-4;++i) if (isPalindrome(string,i,current))	{
				result+=LongMath.pow(2l,string.length-1-current);
				foundPalindrome=true;
				break;
			}
			if (!foundPalindrome) result+=countValidStrings(string,1+current);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long sum=0l;
		long invertedSum=0l;
		for (int i=5;i<=60;++i)	{
			long thisResult=countValidStrings(i);
			long inverted=LongMath.pow(2l,i)-thisResult;
			sum+=thisResult;
			invertedSum+=inverted;
			System.out.println(String.format("%d: F=%d; inv=%d; SF=%d; SInv=%d.",i,thisResult,inverted,sum,invertedSum));
		}
	}
}
