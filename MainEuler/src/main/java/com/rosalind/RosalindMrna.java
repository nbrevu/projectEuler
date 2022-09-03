package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rosalind.aminoacids.Aminoacid;
import com.rosalind.aminoacids.CodonTranslation;

public class RosalindMrna {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_mrna.txt";
	private final static long MOD=1000000l;
	
	private static long getPossibilities(String in,long mod)	{
		long result=1;
		for (char symbol:in.toCharArray())	{
			Aminoacid acid=Aminoacid.fromSymbol(symbol);
			int possibilities=CodonTranslation.howManyCodonsForAminoacid(acid);
			result=(result*possibilities)%mod;
		}
		return (result*CodonTranslation.howManyCodonsAreStop())%mod;
	}
	
	public static void main(String[] args) throws IOException	{
		String input;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			input=reader.readLine();
		}
		System.out.println(getPossibilities(input,MOD));
	}
}
