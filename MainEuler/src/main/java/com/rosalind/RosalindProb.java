package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RosalindProb {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_prob.txt";
	
	private static double calculateProbLog(String str,double probGc)	{
		double logProbGc=Math.log10(probGc/2.0);
		double logProbAt=Math.log10((1.0-probGc)/2.0);
		double result=0;
		for (char symbol:str.toCharArray()) switch (symbol)	{
			case 'A':case 'T':case 'a':case 't':result+=logProbAt;break;
			case 'C':case 'G':case 'c':case 'g':result+=logProbGc;break;
			default:throw new IllegalArgumentException();
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		String baseString;
		double[] gcProbs;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			baseString=reader.readLine();
			String[] probStrings=reader.readLine().split(" ");
			gcProbs=new double[probStrings.length];
			for (int i=0;i<probStrings.length;++i) gcProbs[i]=Double.parseDouble(probStrings[i]);
		}
		for (double prob:gcProbs)	{
			double probLog=calculateProbLog(baseString,prob);
			System.out.print(probLog+" ");
		}
		System.out.println();
	}
}
