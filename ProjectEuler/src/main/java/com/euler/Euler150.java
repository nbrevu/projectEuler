package com.euler;

import com.euler.common.Timing;

public class Euler150 {
	private final static int SIZE=1000;
	private final static long MOD=1l<<20;
	private final static long SEMIMOD=1l<<19;
	
	private static long[][] generateTriangle()	{
		long[][] result=new long[SIZE][];
		for (int i=0;i<SIZE;++i) result[i]=new long[1+i];
		long t=0;
		int row=0;
		int col=0;
		do	{
			t=(615949*t+797807)%MOD;
			result[row][col]=t-SEMIMOD;
			if (col>=row)	{
				col=0;
				++row;
			}	else ++col;
		}	while (row<SIZE);
		return result;
	}
	
	private static long min(long[] array)	{
		long result=array[0];
		for (int i=1;i<array.length;++i) result=Long.min(result,array[i]);
		return result;
	}
	
	private static long solve()	{
		long[][] triangle=generateTriangle();
		long[][] tMinus2=new long[SIZE][1];
		long minVal=Long.MAX_VALUE;
		for (int j=0;j<SIZE;++j)	{
			tMinus2[j][0]=triangle[SIZE-1][j];
			minVal=Math.min(minVal,tMinus2[j][0]);
		}
		long[][] tMinus1=new long[SIZE-1][2];
		for (int j=0;j<SIZE-1;++j)	{
			tMinus1[j][0]=triangle[SIZE-2][j];
			tMinus1[j][1]=triangle[SIZE-2][j]+triangle[SIZE-1][j]+triangle[SIZE-1][j+1];
			minVal=Math.min(minVal,min(tMinus1[j]));
		}
		for (int i=SIZE-3;i>=0;--i)	{
			int maxDepth=SIZE-i;
			long[][] tCurrent=new long[1+i][maxDepth];
			for (int j=0;j<=i;++j)	{
				tCurrent[j][0]=triangle[i][j];
				tCurrent[j][1]=triangle[i][j]+triangle[i+1][j]+triangle[i+1][j+1];
				for (int k=2;k<maxDepth;++k) tCurrent[j][k]=tCurrent[j][0]+tMinus1[j][k-1]+tMinus1[j+1][k-1]-tMinus2[j+1][k-2];
				minVal=Math.min(minVal,min(tCurrent[j]));
			}
			tMinus2=tMinus1;
			tMinus1=tCurrent;
		}
		return minVal;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler150::solve);
	}
}
