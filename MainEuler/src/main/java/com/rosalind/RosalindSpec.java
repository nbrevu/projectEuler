package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.rosalind.aminoacids.Aminoacid;
import com.rosalind.aminoacids.AminoacidData;

public class RosalindSpec {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_spec.txt";
	
	public static void main(String[] args) throws IOException	{
		List<Double> masses=new ArrayList<>();
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			for (;;)	{
				String in=reader.readLine();
				if (in!=null) masses.add(Double.parseDouble(in));
				else break;
			}
		}
		StringBuilder result=new StringBuilder();
		for (int i=1;i<masses.size();++i)	{
			double diff=masses.get(i)-masses.get(i-1);
			Aminoacid acid=AminoacidData.getNearestAminoacid(diff);
			result.append(acid.symbol);
		}
		System.out.println(result.toString());
	}
}
