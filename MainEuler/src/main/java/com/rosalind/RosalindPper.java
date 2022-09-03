package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RosalindPper {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_pper.txt";
	private final static long MOD=1000000l;
	
	private static long variations(long n,long k,long mod)	{
		if (k>n) return 0l;
		long result=1l;
		for (long i=1;i<=k;++i)	{
			result*=n;
			result%=mod;
			--n;
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		long n,k;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			String input=reader.readLine();
			String[] split=input.split(" ");
			n=Long.parseLong(split[0]);
			k=Long.parseLong(split[1]);
		}
		System.out.println(variations(n,k,MOD));
	}
}
