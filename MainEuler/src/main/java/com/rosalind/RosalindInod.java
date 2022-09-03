package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RosalindInod {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_inod.txt";
	
	public static void main(String[] args) throws IOException	{
		Path srcFile=Paths.get(FILE);
		int n;
		try (BufferedReader reader=Files.newBufferedReader(srcFile))	{
			n=Integer.parseInt(reader.readLine());
		}
		// WHY DO YOU DO THIS TO ME, PROJECT ROSALIND? WHY MUST YOU BE SO NOT LIKE PROJECT EULER?
		System.out.println(n-2);
	}
}
