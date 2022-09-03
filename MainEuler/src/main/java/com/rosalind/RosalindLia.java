package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.google.common.math.IntMath;

public class RosalindLia {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_lia.txt";
	
	// The probability is always 25%!
	private static double getProbability(int n,int k)	{
		CombinatorialNumberCache combinatorials=new CombinatorialNumberCache(n);
		double result=0.0;
		for (int i=k;i<=n;++i) result+=combinatorials.get(n,i)*Math.pow(0.25,i)*Math.pow(0.75,n-i);
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		int n,k;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			String[] split=reader.readLine().split(" ");
			n=Integer.parseInt(split[0]);
			k=Integer.parseInt(split[1]);
		}
		System.out.println(getProbability(IntMath.pow(2,n),k));
	}
}
