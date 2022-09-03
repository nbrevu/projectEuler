package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.euler.common.EulerUtils;

public class RosalindSset {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_sset.txt";
	private final static long MOD=1000000l;

	public static void main(String[] args) throws IOException	{
		long in;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			in=Long.parseLong(reader.readLine());
		}	catch (IOException exc)	{
			in=3l;
		}
		System.out.println(EulerUtils.expMod(2l,in,MOD));
	}
}
