package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RosalindIev {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_iev.txt";

	private static double calculateOffspring(long[] couples)	{
		// 1: Always AA.
		// 2: 50% AA, 50% Aa -> Always has the allele.
		// 3: Always Aa.
		// 4: 25% AA, 50% Aa, 25% aa -> 75% of having the allele.
		// 5: 50% Aa, 50% aa -> 50% of having the allele.
		// 6: Always aa.
		double[] coeffs=new double[]{2.0,2.0,2.0,1.5,1.0,0.0};
		double result=0.0;
		for (int i=0;i<6;++i) result+=coeffs[i]*couples[i];
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		long[] couples=new long[6];
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			String line=reader.readLine();
			String[] split=line.split(" ");
			for (int i=0;i<6;++i) couples[i]=Long.parseLong(split[i]);
		}
		System.out.println(calculateOffspring(couples));
	}
}
