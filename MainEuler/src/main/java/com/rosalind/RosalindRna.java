package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RosalindRna {
	public static String convertRna(String in)	{
		StringBuilder result=new StringBuilder();
		for (char ch:in.toCharArray()) switch (ch)	{
			case 'a':case 'A':result.append('A');break;
			case 'c':case 'C':result.append('C');break;
			case 'g':case 'G':result.append('G');break;
			case 't':case 'T':result.append('U');break;
			default:throw new IllegalStateException();
		}
		return result.toString();
	}

	public static void main(String[] args) throws IOException	{
		Path srcFile=Paths.get("F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_rna.txt");
		try (BufferedReader reader=Files.newBufferedReader(srcFile))	{
			for (;;)	{
				String line=reader.readLine();
				if (line==null) return;
				String converted=convertRna(line);
				System.out.println(converted);
			}
		}
	}
}
