package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.rosalind.utils.Alphabet;

public class RosalindLexf {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_lexf.txt";
	
	private static class StringGenerator	{
		private final Alphabet alphabet;
		private final int[] positions;
		public StringGenerator(Alphabet alphabet,int size)	{
			this.alphabet=alphabet;
			positions=new int[size];
			positions[size-1]=-1;	// Ugly hack, but does the work.
		}
		public String nextString()	{
			int N=alphabet.getLength();
			int i=positions.length-1;
			for (;;--i)	{
				if (i<0) return null;
				else if (positions[i]<N-1)	{
					++positions[i];
					break;
				}
			}
			for (++i;i<positions.length;++i) positions[i]=0;
			return getString();
		}
		private String getString()	{
			StringBuilder builder=new StringBuilder();
			for (int i=0;i<positions.length;++i) builder.append(alphabet.getAt(positions[i]));
			return builder.toString();
		}
	}

	public static void main(String[] args) throws IOException	{
		Path srcFile=Paths.get(FILE);
		Alphabet alph;
		int number;
		try (BufferedReader reader=Files.newBufferedReader(srcFile))	{
			alph=Alphabet.readSymbols(reader.readLine());
			number=Integer.parseInt(reader.readLine());
		}
		StringGenerator gen=new StringGenerator(alph,number);
		for (;;)	{
			String s=gen.nextString();
			if (s==null) break;
			System.out.println(s);
		}
	}
}
