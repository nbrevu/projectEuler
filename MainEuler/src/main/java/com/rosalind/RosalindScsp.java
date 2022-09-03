package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RosalindScsp {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_scsp.txt";
	
	private static int[][] getMinLengths(String s1,String s2)	{
		int N=s1.length();
		int M=s2.length();
		int[][] result=new int[N+1][M+1];
		for (int i=1;i<=N;++i) result[i][0]=i;
		for (int j=1;j<=M;++j) result[0][j]=j;
		for (int i=1;i<=N;++i) for (int j=1;j<=M;++j) if (s1.charAt(i-1)==s2.charAt(j-1)) result[i][j]=1+result[i-1][j-1];
		else result[i][j]=1+Math.min(result[i][j-1],result[i-1][j]);
		return result;
	}
	
	private static String backtrack(int[][] minLengths,String s1,String s2)	{
		StringBuilder result=new StringBuilder();
		int i=s1.length();
		int j=s2.length();
		while ((i>0)&&(j>0)) if (s1.charAt(i-1)==s2.charAt(j-1))	{
			result.append(s1.charAt(i-1));
			--i;
			--j;
		}	else if (minLengths[i-1][j]>minLengths[i][j-1])	{
			result.append(s2.charAt(j-1));
			--j;
		}	else	{
			result.append(s1.charAt(i-1));
			--i;
		}
		while (i>0)	{
			result.append(s1.charAt(i-1));
			--i;
		}
		while (j>0)	{
			result.append(s2.charAt(j-1));
			--j;
		}
		return result.reverse().toString();
	}
	
	public static void main(String[] args) throws IOException	{
		String s1,s2;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			s1=reader.readLine();
			s2=reader.readLine();
		}
		int[][] minLengths=getMinLengths(s1,s2);
		System.out.println(backtrack(minLengths,s1,s2));
	}
}
