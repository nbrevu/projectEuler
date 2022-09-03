package com.euler;

public class Euler310_3 {
	private final static int LIMIT=29;
	
	private static boolean[] classifyNumbers(int limit)	{
		boolean[] result=new boolean[1+limit];
		result[0]=false;
		for (int i=1;i<=limit;++i) for (int r=1;;++r)	{
			int r2=r*r;
			if (r2>i) break;
			else if (!result[i-r2])	{
				result[i]=true;
				break;
			}
		}
		return result;
	}
	
	private static long getCombinations(boolean[] classified)	{
		/*
		long losing=0;
		long winning=0;
		for (int i=0;i<classified.length;++i) if (classified[i]) ++winning;
		else ++losing;
		// Combinations of three different elements: either 2 winning/0 losing or 0 winning/3 losing.
		long result=(winning*(winning-1)*losing)/2+(losing*(losing-1)*(losing-2))/6;
		// Combinations of two equal and a different element: one of them (the repeated one) must be a
		// winning, the other one must be a losing.
		result+=winning*losing;
		// Combinations of the same element three times: it must be a losing one.
		return result+losing;
		*/
		long result=0;
		for (int c=0;c<classified.length;++c) for (int b=0;b<=c;++b) for (int a=0;a<=b;++a) if ((classified[a]!=classified[b])==classified[c]) ++result;
		return result;
	}
	
	private static long calculate(int limit)	{
		return getCombinations(classifyNumbers(limit));
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=200;++i) System.out.println(""+i+" => "+calculate(i));
	}
}