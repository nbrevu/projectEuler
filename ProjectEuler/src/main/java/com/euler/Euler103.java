package com.euler;

import com.euler.common.Timing;

public class Euler103 {
	private final static int[] PREVIOUS=new int[] {11,18,19,20,22,25};
	
	private static String solve()	{
		int middle=PREVIOUS.length/2;
		StringBuilder result=new StringBuilder();
		result.append(PREVIOUS[middle]);
		for (int p:PREVIOUS) result.append(PREVIOUS[middle]+p);
		return result.toString();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler103::solve);
	}
}
