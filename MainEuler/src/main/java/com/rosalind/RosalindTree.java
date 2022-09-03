package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RosalindTree {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_subs.txt";
	
	private static List<Integer> getPositions(String baseString,String substring)	{
		List<Integer> result=new ArrayList<>();
		int position=0;
		for (;;)	{
			int newPos=baseString.indexOf(substring,position);
			if (newPos>0)	{
				result.add(1+newPos);
				position=1+newPos;
			}	else break;
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		String baseString;
		String substring;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			baseString=reader.readLine();
			substring=reader.readLine();
		}	catch (IOException exc)	{
			baseString="GATATATGCATATACTT";
			substring="ATAT";
		}
		List<Integer> result=getPositions(baseString,substring);
		if (!result.isEmpty())	{
			System.out.print(result.get(0));
			for (int i=1;i<result.size();++i)	{
				System.out.print(' ');
				System.out.print(result.get(i));
			}
			System.out.println();
		}
	}
}
