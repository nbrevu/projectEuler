package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RosalindFibd {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_fibd.txt";
	
	private static long fib(int n,int m)	{
		long[] mature=new long[1+n];
		long[] born=new long[1+n];
		mature[1]=0l;
		born[1]=1l;
		for (int i=2;i<=n;++i)	{
			born[i]=mature[i-1];
			mature[i]=mature[i-1]+born[i-1];
			long dying=0;
			int dyingIndex=i-m;
			if (dyingIndex>=1) dying=born[dyingIndex];
			mature[i]-=dying;
		}
		return born[n]+mature[n];
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
				int m=Integer.parseInt(numberStrings[1]);
				System.out.println(fib(n,m));
			}
		}
	}
}
