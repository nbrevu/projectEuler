package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RosalindFib {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_fib.txt";
	
	private static long fib(int n,long k)	{
		if (n<=2) return 1l;
		long fibN=1l;
		long fibN_=1l;
		for (int i=3;i<=n;++i)	{
			long nextFib=fibN+k*fibN_;
			fibN_=fibN;
			fibN=nextFib;
		}
		return fibN;
	}
	
	public static void main(String[] args) throws IOException	{
		Path srcFile=Paths.get(FILE);
		try (BufferedReader reader=Files.newBufferedReader(srcFile))	{
			for (;;)	{
				String line=reader.readLine();
				if (line==null) return;
				String[] numberStrings=line.split(" ");
				if (numberStrings.length!=2) throw new RuntimeException("Unexpected data.");
				int n=Integer.parseInt(numberStrings[0]);
				int k=Integer.parseInt(numberStrings[1]);
				System.out.println(fib(n,k));
			}
		}
	}
}
