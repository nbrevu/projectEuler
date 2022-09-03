package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.aminoacids.CodonTranslation;
import com.rosalind.aminoacids.TranslatedCodon;

public class RosalindProt {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_prot.txt";
	
	private static String translateCodons(String in)	{
		List<TranslatedCodon> translated=CodonTranslation.translateUntilStop(in,false);
		StringBuilder result=new StringBuilder();
		for (TranslatedCodon codon:translated) result.append(codon.getSymbol());
		return result.toString();
	}

	public static void main(String[] args) throws IOException	{
		String input;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			input=reader.readLine();
		}
		System.out.println(translateCodons(input));
	}
}
