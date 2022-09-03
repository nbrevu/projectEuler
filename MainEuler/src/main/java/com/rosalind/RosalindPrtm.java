package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.aminoacids.AminoacidData;

public class RosalindPrtm {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_prtm.txt";
	
	public static void main(String[] args) throws IOException	{
		String input;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			input=reader.readLine();
		}	catch (IOException exc)	{
			input="SKADYEK";
		}
		System.out.println(AminoacidData.getMass(input));
	}
}
