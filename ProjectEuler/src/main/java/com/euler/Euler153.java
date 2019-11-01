package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler153 {
	private final static int LIMIT=IntMath.pow(10,8);
	
	private static long solve()	{
		long result=0;
		for (int i=1;i<=LIMIT;++i) result+=i*(LIMIT/i);
		int sqrtL=IntMath.sqrt(LIMIT,RoundingMode.UP);
		int[] squares=new int[1+sqrtL];
		for (int i=1;i<=sqrtL;++i) squares[i]=i*i;
		for (int i=1;i<sqrtL;++i) for (int j=(i>1)?(i+1):i;j<=sqrtL;++j) if (EulerUtils.areCoprime(i,j))	{
			int sumS=squares[i]+squares[j];
			if (sumS>LIMIT) break;
			int sum=(j==i)?2:(2*(i+j));	// i==j only happens with i=1, j=1. It should be "i+j", but well. 1+1=2 and all that shit.
			int nLim=LIMIT/sumS;
			result+=sum*nLim;
			for (int k=2;k<=nLim;++k) result+=k*sum*(LIMIT/(k*sumS));
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler153::solve);
	}
}