package com.euler;

import com.euler.common.Timing;

public class Euler149 {
	private final static int SIZE=2000;
	
	private final static long MOD=1000000l;
	private final static long SEMIMOD=500000l;
	private final static int LAG1=24;
	private final static int LAG2=55;
	
	private static long[][] generateMatrix()	{
		long[][] result=new long[SIZE][SIZE];
		for (int j=1;j<=LAG2;++j)	result[0][j-1]=((100003l+j*(-200003l+j*j*300007l))%MOD)-SEMIMOD;
		for (int j=LAG2;j<SIZE;++j) result[0][j]=(result[0][j-LAG1]+result[0][j-LAG2]+MOD)%MOD-SEMIMOD;
		for (int i=1;i<SIZE;++i)	{
			for (int j=0;j<LAG1;++j) result[i][j]=(result[i-1][j+SIZE-LAG1]+result[i-1][j+SIZE-LAG2]+MOD)%MOD-SEMIMOD;
			for (int j=LAG1;j<LAG2;++j) result[i][j]=(result[i][j-LAG1]+result[i-1][j+SIZE-LAG2]+MOD)%MOD-SEMIMOD;
			for (int j=LAG2;j<SIZE;++j) result[i][j]=(result[i][j-LAG1]+result[i][j-LAG2]+MOD)%MOD-SEMIMOD;
		}
		return result;
	}
	
	private static long maxSum(long[][] matrix,int startI,int startJ,int incI,int incJ,int size)	{
		long result=0;
		int i=startI;
		int j=startJ;
		long curSum=Math.max(matrix[i][j],0);
		for (int k=1;k<size;++k)	{
			i+=incI;
			j+=incJ;
			long newVal=matrix[i][j];
			curSum=Math.max(curSum+newVal,0);
			result=Math.max(curSum,result);
		}
		return result;
	}
	
	private static long solve()	{
		long[][] matrix=generateMatrix();
		long result=0l;
		// Horizontal.
		for (int i=0;i<SIZE;++i) result=Math.max(result,maxSum(matrix,i,0,0,1,SIZE));
		// Vertical.
		for (int j=0;j<SIZE;++j) result=Math.max(result,maxSum(matrix,0,j,1,0,SIZE));
		// Diagonal (-45 from the horizontal)
		for (int i=0;i<SIZE;++i) result=Math.max(result,maxSum(matrix,i,0,1,1,SIZE-i));
		for (int j=1;j<SIZE;++j) result=Math.max(result,maxSum(matrix,0,j,1,1,SIZE-j));
		// Diagonal (+45 from the horizontal)
		for (int i=0;i<SIZE;++i) result=Math.max(result,maxSum(matrix,i,0,-1,1,i+1));
		for (int j=1;j<SIZE;++j) result=Math.max(result,maxSum(matrix,SIZE-1,j,-1,1,SIZE-j));
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler149::solve);
	}
}
