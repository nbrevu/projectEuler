package com.rosalind;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindKmp {
	// ZUTUN! Esto no está terminado... tengo mis sospechas acerca de cómo podría arreglasrse, pero no lo tengo claro.
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_kmp.txt";
	
	private static int[] kmpSearch(String S)	{
		int[] b=new int[S.length()];
		{
			// Table creation.
			int i=0;
			int j=-1;
			b[i]=j;
			while (i<S.length()-1)	{
				while ((j>=0)&&(S.charAt(i)!=S.charAt(j))) j=b[j];
				++i;
				++j;
				b[i]=j;
			}
		}	{
			int[] result=new int[S.length()];
			// Search itself.
			int i=0;
			int j=-1;
			while (i<S.length()-1)	{
				while ((j>=0)&&(S.charAt(i)!=S.charAt(j))) j=b[j];
				++i;
				++j;
				result[i]=j;
				if (j==S.length()) j=b[j];
			}
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		// ZUTUN! Pues está mal :O.
		try (PrintStream out=new PrintStream("F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\OutFiles\\rosalind_kmp.txt"))	{
			FastaReader reader=new FastaReader(Paths.get(FILE));
			reader.read();
			List<FastaEntry> entries=reader.getEntries();
			String singleton=entries.get(0).getContent();
			int[] result=kmpSearch(singleton);
			for (int v:result) out.print(v+" ");
			out.println();
		}
	}
}
