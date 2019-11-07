package com.euler;

import java.util.Arrays;

import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler159 {
	private final static int N=IntMath.pow(10,6);
	
	private static int getDigitalSum(int in)	{
		return 1+((in-1)%9);
	}
	
	private static long solve()	{
		int[] mdrs=new int[N];
		Arrays.setAll(mdrs,Euler159::getDigitalSum);
		long result=0;
		for (int i=2;i<N;++i)	{
			int current=mdrs[i];
			int j=2;
			int n=i+i;
			while ((j<=i)&&(n<N))	{
				mdrs[n]=Math.max(mdrs[n],current+mdrs[j]);
				++j;
				n+=i;
			}
			result+=current;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler159::solve);
	}
}
