package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.utils.IoUtils;

public class RosalindEbin {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_ebin.txt";
	
	// Is this a joke?
	private static double[] multiply(double in,double[] array)	{
		double[] result=new double[array.length];
		for (int i=0;i<array.length;++i) result[i]=in*array[i];
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		int n;
		double[] probs;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			n=Integer.parseInt(reader.readLine());
			probs=IoUtils.parseStringAsArrayOfDoubles(reader.readLine(),-1);
		}
		double[] result=multiply((double)n,probs);
		System.out.println(IoUtils.toStringWithSpaces(result));
	}
}
