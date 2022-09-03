package com.euler;

import java.util.OptionalInt;

public class Euler750 {
	private final static int N=976;
	
	public static void main(String[] args)	{
		int[] numbers=new int[N];
		int p=1;
		for (int i=1;i<=N;++i)	{
			p*=3;
			numbers[i-1]=p%=N+1;
		}
		/*
		 * Initial estimation of the heuristic: smallest distance to a higher card.
		 * Too low. This might not be workable :(.
		 */
		long result=0l;
		for (int i=0;i<numbers.length;++i)	{
			int value=numbers[i];
			OptionalInt minLeft=OptionalInt.empty();
			OptionalInt minRight=OptionalInt.empty();
			for (int j=i-1;j>=0;--j) if (numbers[j]>value)	{
				minLeft=OptionalInt.of(i-j);
				break;
			}
			for (int j=i+1;j<numbers.length;++j) if (numbers[j]>value)	{
				minRight=OptionalInt.of(j-i);
				break;
			}
			long distance=0l;
			if (minLeft.isPresent()&&minRight.isPresent()) distance=Math.min(minLeft.getAsInt(),minRight.getAsInt());
			else if (minLeft.isPresent()) distance=minLeft.getAsInt();
			else if (minRight.isPresent()) distance=minRight.getAsInt();
			result+=distance;
		}
		System.out.println(result);
	}
}
