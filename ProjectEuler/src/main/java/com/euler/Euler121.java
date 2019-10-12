package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;

import com.euler.common.EulerUtils;
import com.euler.common.Timing;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler121 {
	private final static int TURNS=15;
	
	private static long factorial(long in)	{
		long result=1l;
		for (long i=2;i<=in;++i) result*=i;
		return result;
	}
	
	private static long accumulateNumerators(int howMany,int N)	{
		if (N==howMany) return 1l;
		int[] array=new int[N];
		Arrays.fill(array,N-howMany,N,1);
		long result=0l;
		do	{
			long thisProduct=1;
			for (int i=0;i<N;++i) if (array[i]==0) thisProduct*=i+1;
			result+=thisProduct;
		}	while (EulerUtils.nextPermutation(array));
		return result;
	}
	
	private static long solve()	{
		long den=factorial(1+TURNS);
		int minTurns=1+IntMath.divide(TURNS,2,RoundingMode.DOWN);
		long totalNum=0;
		for (int i=minTurns;i<=TURNS;++i) totalNum+=accumulateNumerators(i,TURNS);
		return LongMath.divide(den,totalNum,RoundingMode.DOWN);
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler121::solve);
	}
}
