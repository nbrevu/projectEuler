package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.euler.common.EulerUtils.CombinatorialNumberModCache;

public class RosalindAspc {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_aspc.txt";
	private final static long MOD=1000000l;
	
	public static void main(String[] args) throws IOException	{
		int n,m;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			String line=reader.readLine();
			String[] split=line.split(" ");
			n=Integer.parseInt(split[0]);
			m=Integer.parseInt(split[1]);
		}
		CombinatorialNumberModCache cache=new CombinatorialNumberModCache(n,MOD);
		long result=0;
		for (int k=m;k<=n;++k) result=(result+cache.get(n,k))%MOD;
		System.out.println(result);
	}
}
