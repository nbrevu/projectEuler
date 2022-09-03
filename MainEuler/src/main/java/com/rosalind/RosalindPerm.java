package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.rosalind.utils.PermutationGenerator;

public class RosalindPerm {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_perm.txt";
	
	private static long getFactorial(long in)	{
		long result=1;
		for (long i=2;i<=in;++i) result*=i;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		Path srcFile=Paths.get(FILE);
		int number;
		try (BufferedReader reader=Files.newBufferedReader(srcFile))	{
			number=Integer.parseInt(reader.readLine());
		}
		System.out.println(getFactorial(number));
		PermutationGenerator gen=new PermutationGenerator(number);
		for (;;)	{
			String perm=gen.getNext();
			if (perm==null) break;
			else System.out.println(perm);
		}
	}
}
