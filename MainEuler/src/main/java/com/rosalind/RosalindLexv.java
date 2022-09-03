package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.rosalind.utils.Alphabet;

public class RosalindLexv {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_lexv.txt";
	
	private static class StringGenerator	{
		private final Alphabet alphabet;
		private final List<Integer> positions;
		private final int maxSize;
		public StringGenerator(Alphabet alphabet,int maxSize)	{
			this.alphabet=alphabet;
			positions=new ArrayList<>();
			this.maxSize=maxSize;
		}	
		public String nextString()	{
			int N=alphabet.getLength();
			if (positions.size()<maxSize) positions.add(0);
			else	{
				for (;;)	{
					int lastValue=positions.get(positions.size()-1);
					if (lastValue<N-1)	{
						positions.set(positions.size()-1,1+lastValue);
						break;
					}	else	{
						positions.remove(positions.size()-1);
						if (positions.isEmpty()) return null;
					}
				}
			}
			return getString();
		}
		private String getString()	{
			StringBuilder builder=new StringBuilder();
			for (int idx:positions) builder.append(alphabet.getAt(idx));
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
