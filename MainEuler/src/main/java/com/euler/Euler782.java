package com.euler;

import java.util.Arrays;

import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler782 {
	private static int sum(boolean[][] matrix)	{
		int result=0;
		for (int i=0;i<matrix.length;++i) for (int j=0;j<matrix.length;++j) if (matrix[i][j]) ++result;
		return result;
	}
	
	private static int extractRow(boolean[][] matrix,int row)	{
		int result=0;
		for (int i=0;i<matrix.length;++i)	{
			result<<=1;
			if (matrix[row][i]) result|=1;
		}
		return result;
	}
	
	private static int extractColumn(boolean[][] matrix,int col)	{
		int result=0;
		for (int i=0;i<matrix.length;++i)	{
			result<<=1;
			if (matrix[i][col]) result|=1;
		}
		return result;
	}
	
	private final static IntSet values=HashIntSets.newMutableSet(12);
	private static int complexity(boolean[][] matrix)	{
		values.clear();
		for (int i=0;i<matrix.length;++i)	{
			values.add(extractRow(matrix,i));
			values.add(extractColumn(matrix,i));
		}
		return values.size();
	}
	
	private static boolean nextMatrix(boolean[][] matrix)	{
		for (int i=0;i<matrix.length;++i) for (int j=0;j<matrix.length;++j)	{
			matrix[i][j]=!matrix[i][j];
			if (matrix[i][j]) return true;
		}
		return false;
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=6;++i)	{
			boolean[][] matrix=new boolean[i][i];
			int[] minimumComplexity=new int[1+i*i];
			Arrays.fill(minimumComplexity,Integer.MAX_VALUE);
			do	{
				int k=sum(matrix);
				int c=complexity(matrix);
				minimumComplexity[k]=Math.min(c,minimumComplexity[k]);
			}	while (nextMatrix(matrix));
			int c=0;
			for (int j=0;j<minimumComplexity.length;++j)	{
				System.out.println(String.format("c(%d,%d)=%d.",i,j,minimumComplexity[j]));
				c+=minimumComplexity[j];
			}
			System.out.println(String.format("C(%d)=%d.",i,c));
		}
	}
}
