package com.euler;

import java.math.RoundingMode;

import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler135 {
	private final static int LIMIT=IntMath.pow(10,6);
	private final static int GOAL=10;
	
	private static long solve()	{
		int[] counters=new int[1+LIMIT];
		int limitA=(int)Math.floor(Math.sqrt(3*LIMIT));
		for (int a=1;a<=limitA;++a)	{
			int b0=a/3;
			b0+=4-((a+b0)%4);
			if (3*b0<=a) b0+=4;
			int bf=IntMath.divide(LIMIT,a,RoundingMode.DOWN);
			for (int b=b0;b<=bf;b+=4) ++counters[a*b];
		}
		int result=0;
		for (int i=1;i<=LIMIT;++i) if (counters[i]==GOAL) ++result;
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler135::solve);
	}
}
