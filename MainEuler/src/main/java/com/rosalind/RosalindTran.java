package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindTran {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_tran.txt";
	
	private static enum Base	{
		A('A','G',Arrays.asList('C','T')),
		C('C','T',Arrays.asList('A','G')),
		G('G','A',Arrays.asList('C','T')),
		T('T','C',Arrays.asList('A','G'));
		
		private final char id;
		private final char transition;
		private final List<Character> transversions;
		
		private Base(char id,char transition,List<Character> transversions)	{
			this.id=id;
			this.transition=transition;
			this.transversions=transversions;
		}
		
		private final static Map<Character,Base> BASE_MAP=new HashMap<>();
		static	{
			for (Base base:values()) BASE_MAP.put(base.id,base);
		}
		public static Base getFromCharacter(Character in)	{
			return BASE_MAP.get(in);
		}
		public boolean isTransition(char in)	{
			return in==transition;
		}
		public boolean isTransversion(Character in)	{
			return transversions.contains(in);
		}
	}
	
	private static double getTransitionToTransversionRatio(String s1,String s2)	{
		int N=s1.length();
		if (s2.length()!=N) throw new IllegalArgumentException();
		int transitions=0;
		int transversions=0;
		for (int i=0;i<N;++i)	{
			char c1=s1.charAt(i);
			char c2=s2.charAt(i);
			if (c1==c2) continue;
			Base base=Base.getFromCharacter(c1);
			if (base.isTransition(c2)) ++transitions;
			else if (base.isTransversion(c2)) ++transversions;
			else throw new IllegalArgumentException();
		}
		return (double)transitions/(double)transversions;
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=2) throw new IllegalArgumentException();
		System.out.println(getTransitionToTransversionRatio(entries.get(0).getContent(),entries.get(1).getContent()));
	}
}
