package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;

import com.google.common.math.IntMath;

public class Euler535 {
	private static int[] generateFractalSequence(int size)	{
		int[] result=new int[size];
		if (size<=0) return result;
		result[0]=1;
		result[1]=1;
		int currentIndex=2;
		int nextCircledDigit=2;
		int currentReference=1;
		boolean canContinue=true;
		while (canContinue)	{
			int nextDigit=result[currentReference];
			++currentReference;
			int extraNumbers=IntMath.sqrt(nextDigit,RoundingMode.DOWN);
			for (int i=0;i<extraNumbers;++i)	{
				result[currentIndex]=nextCircledDigit;
				++nextCircledDigit;
				++currentIndex;
				if (currentIndex>=size)	{
					canContinue=false;
					break;
				}
			}
			if (!canContinue) break;
			result[currentIndex]=nextDigit;
			++currentIndex;
			canContinue=(currentIndex<size);
		}
		System.out.println("I've needed "+currentReference+" base elements.");
		System.out.println("The last \"circled\" element I've used is "+(nextCircledDigit-1)+".");
		return result;
	}
	
	/*
	 * It seems that I need at least two levels of indirection here. One is not enough. I would need more than 10^12 base elements; not viable.
	 * With 2 levels of indirection, I believe that it can be done.
	 */
	public static void main(String[] args)	{
		int N=1000;
		int[] seq=generateFractalSequence(N);
		System.out.println(Arrays.toString(seq));
		long sum=Arrays.stream(seq).mapToLong((int x)->x).sum();
		System.out.println(sum);
	}
}
