package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.utils.Generation;
import com.rosalind.utils.IoUtils;

public class RosalindFoun {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_foun.txt";

	private static double[][] getB(int N,int m,int[] A)	{
		int size=A.length;
		double[][] result=new double[m][size];
		for (int j=0;j<size;++j)	{
			Generation g=Generation.getFixedGeneration(N,A[j]);
			for (int i=0;i<m;++i)	{
				g=g.nextGeneration();
				result[i][j]=Math.log10(g.getProb(0));
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		int N,m;
		int[] A;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			int[] data=IoUtils.parseStringAsArrayOfInts(reader.readLine(),2);
			A=IoUtils.parseStringAsArrayOfInts(reader.readLine(),-1);
			N=data[0];
			m=data[1];
		}
		double[][] B=getB(N,m,A);
		for (int i=0;i<B.length;++i) System.out.println(IoUtils.toStringWithSpaces(B[i]));
	}
}
