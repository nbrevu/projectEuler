package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RosalindCunr {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_cunr.txt";
	private final static long MOD=1000000l;
	
	private static long doubleFactorial(long in,long mod)	{
		long result=1l;
		for (;in>2;in-=2) result=(result*in)%mod;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		int in;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			in=Integer.parseInt(reader.readLine());
		}	catch (IOException exc)	{
			in=5;
		}
		System.out.println(doubleFactorial(2*in-5,MOD));
	}
}
