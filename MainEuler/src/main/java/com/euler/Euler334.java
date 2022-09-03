package com.euler;

import java.util.Arrays;

public class Euler334 {
	public static void main(String[] args)	{
		int[] array=new int[2000];
		array[999]=289;
		array[1000]=145;
		int position=999;
		int moveCount=0;
		for (;;)	{
			while ((position<array.length)&&array[position]<2) ++position;
			if (position>=array.length) break;
			array[position]-=2;
			++array[position-1];
			++array[position+1];
			--position;
			++moveCount;
		}
		System.out.println(moveCount);
		System.out.println(Arrays.toString(array));
	}
}
